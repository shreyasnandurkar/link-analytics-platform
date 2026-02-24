import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const createTime = new Trend('create_time');
const redirectTime = new Trend('redirect_time');
const invalidTime = new Trend('invalid_time');

export const options = {
    vus: 500,
    duration: '1m',
};

export function setup() {

    const keys = [];

    for (let i = 0; i < 1000; i++) {

        const payload = JSON.stringify({
            originalUrl: "https://example.com/" + i
        });

        const res = http.post(
            'http://localhost:8080/shorten',
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );

        check(res, { 'setup create 200': r => r.status === 200 });

        const key = res.body.split('/').pop();
        keys.push(key);
    }

    return keys;
}

export default function (keys) {

    const rand = Math.random();

    if (rand < 0.80) {

        const randomKey = keys[Math.floor(Math.random() * keys.length)];

        const res = http.get(
            `http://localhost:8080/${randomKey}`,
            { redirects: 0 }
        );

        redirectTime.add(res.timings.duration);

        check(res, { 'valid redirect 302': r => r.status === 302 });

    } else if (rand < 0.95) {

        const fakeKey = Math.random().toString(36).substring(2, 10);

        const res = http.get(
            `http://localhost:8080/${fakeKey}`,
            { redirects: 0 }
        );

        invalidTime.add(res.timings.duration);

        check(res, { 'invalid redirect 404': r => r.status === 404 });

    } else {

        const payload = JSON.stringify({
            originalUrl: "https://example.com/" + Math.random()
        });

        const res = http.post(
            'http://localhost:8080/shorten',
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );

        createTime.add(res.timings.duration);

        check(res, { 'create 200': r => r.status === 200 });
    }
}