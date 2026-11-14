import { describe, expect, it } from 'vitest';
import { parseHash } from './router';

describe('parseHash', () => {
  it.each([
    ['', { name: 'home' }],
    ['#/', { name: 'home' }],
    ['#/workouts', { name: 'workouts' }],
    ['#/plan', { name: 'plan' }],
    ['#/forum', { name: 'forum' }],
    ['#/admin', { name: 'admin' }],
  ])('maps %j to the right page', (hash, expected) => {
    expect(parseHash(hash)).toEqual(expected);
  });

  it('extracts and decodes the thread id', () => {
    expect(parseHash('#/forum/abc%20123')).toEqual({ name: 'thread', id: 'abc 123' });
  });

  it('tolerates trailing slashes', () => {
    expect(parseHash('#/forum/42/')).toEqual({ name: 'thread', id: '42' });
  });

  it.each(['#/nope', '#/workouts/1', '#/forum/1/edit'])('returns not-found for %j', (hash) => {
    expect(parseHash(hash)).toEqual({ name: 'not-found' });
  });
});
