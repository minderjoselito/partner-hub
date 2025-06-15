import http from 'k6/http';
import encoding from 'k6/encoding';
import { check, sleep } from 'k6';

const username = 'admin';
const password = 'admin';
const credentials = `${username}:${password}`;
const encodedCredentials = encoding.b64encode(credentials);

export let options = {
  vus: 50,
  duration: '30s',
};

export default function () {
  let url = 'http://localhost:8080/api/users';
  let params = {
    headers: {
      'Authorization': `Basic ${encodedCredentials}`,
    },
  };

  let res = http.get(url, params);
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(1);
}
