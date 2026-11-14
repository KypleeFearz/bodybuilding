import { parseHash, type Route } from './router';

class Router {
  route: Route = $state(parseHash(window.location.hash));

  constructor() {
    window.addEventListener('hashchange', () => {
      this.route = parseHash(window.location.hash);
      window.scrollTo({ top: 0 });
    });
  }

  go(path: string): void {
    window.location.hash = path;
  }
}

export const router = new Router();
