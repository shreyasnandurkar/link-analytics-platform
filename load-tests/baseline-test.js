import http from 'k6/http';
import {check, sleep} from 'k6';
import {Trend} from 'k6/metrics';

/* -------------------- Response Classification -------------------- */

/*
  Treat 404 as expected.
  Only real failures (timeouts, 5xx, connection issues) count as http_req_failed.
*/
http.setResponseCallback(
    http.expectedStatuses({min: 200, max: 399}, 404)
);

/* -------------------- Custom Metrics -------------------- */

const redirectTime = new Trend('redirect_time', true);
const invalidTime = new Trend('invalid_time', true);

/* -------------------- Load Profile -------------------- */

export const options = {
    stages: [
        {duration: '5s', target: 1000},
        {duration: '30s', target: 1000},
        {duration: '5s', target: 0},
    ],
    setupTimeout: '180s',
};

/* -------------------- Setup Phase -------------------- */

export function setup() {
    const keys = [];

    for (let i = 0; i < 2000; i++) {
        const payload = JSON.stringify({
            originalUrl: `https://example.com/${i}`,
        });

        const res = http.post(
            'http://localhost:8080/shorten',
            payload,
            {
                headers: {'Content-Type': 'application/json'},
                tags: {type: 'setup'},
            }
        );

        check(res, {
            'setup create 200': r => r.status === 200,
        });

        const key = res.body.split('/').pop();
        keys.push(key);
    }

    for (const key of keys) {
        http.get(`http://localhost:8080/${key}`, {redirects: 0});
    }

    return keys;
}

/* -------------------- Main Test Logic -------------------- */

export default function (keys) {
    const rand = Math.random();

    /* 100% Valid Redirect */
    if (rand < 1.01) {
        const randomKey =
            keys[Math.floor(Math.random() * keys.length)];

        const res = http.get(
            `http://localhost:8080/${randomKey}`,
            {
                redirects: 0,
                tags: {type: 'redirect'},
            }
        );

        redirectTime.add(res.timings.duration);

        check(res, {
            'valid redirect 302': r => r.status === 302,
        });
    }

    /* 0% Invalid Redirect */
    else {
        const fakeKey =
            Math.random().toString(36).substring(2, 10);

        const res = http.get(
            `http://localhost:8080/${fakeKey}`,
            {
                redirects: 0,
                tags: {type: 'invalid'},
            }
        );

        invalidTime.add(res.timings.duration);

        check(res, {
            'invalid redirect 404': r => r.status === 404,
        });
    }

    sleep(0.01);
}
