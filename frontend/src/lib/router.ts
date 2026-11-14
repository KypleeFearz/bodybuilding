export type Route =
  | { name: 'home' }
  | { name: 'workouts' }
  | { name: 'plan' }
  | { name: 'forum' }
  | { name: 'thread'; id: string }
  | { name: 'admin' }
  | { name: 'not-found' };

/** Maps a location hash like `#/forum/42` to a route. Hash routing needs no server-side rewrite rules. */
export function parseHash(hash: string): Route {
  const segments = hash.replace(/^#/, '').split('/').filter(Boolean);
  const [first, second, ...rest] = segments;

  if (rest.length > 0) return { name: 'not-found' };

  switch (first) {
    case undefined:
      return { name: 'home' };
    case 'workouts':
      return second ? { name: 'not-found' } : { name: 'workouts' };
    case 'plan':
      return second ? { name: 'not-found' } : { name: 'plan' };
    case 'forum':
      return second ? { name: 'thread', id: decodeURIComponent(second) } : { name: 'forum' };
    case 'admin':
      return second ? { name: 'not-found' } : { name: 'admin' };
    default:
      return { name: 'not-found' };
  }
}

export function href(path: string): string {
  return `#${path}`;
}
