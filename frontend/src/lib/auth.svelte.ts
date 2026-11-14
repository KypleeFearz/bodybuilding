import { createAuth0Client, type Auth0Client, type User } from '@auth0/auth0-spa-js';
import { api, setTokenProvider } from './api';
import type { ClientConfig, Me } from './types';

/**
 * Global authentication state. Uses Auth0 Universal Login with PKCE and sends access tokens (not ID tokens)
 * to the API. When Auth0 is not configured the app runs in read-only demo mode.
 */
class AuthState {
  enabled = $state(false);
  ready = $state(false);
  user: User | null = $state(null);
  me: Me | null = $state(null);

  isAdmin = $derived(this.me?.admin ?? false);

  #client: Auth0Client | null = null;

  async init(config: ClientConfig): Promise<void> {
    this.enabled = config.authEnabled;
    if (!config.authEnabled) {
      this.ready = true;
      return;
    }

    this.#client = await createAuth0Client({
      domain: config.authDomain,
      clientId: config.authClientId,
      authorizationParams: {
        audience: config.authAudience,
        redirect_uri: window.location.origin,
      },
      cacheLocation: 'localstorage',
      useRefreshTokens: true,
    });

    const params = new URLSearchParams(window.location.search);
    if (params.has('code') && params.has('state')) {
      const { appState } = await this.#client.handleRedirectCallback<{ hash?: string }>();
      window.history.replaceState({}, document.title, window.location.pathname + (appState?.hash ?? ''));
    }

    if (await this.#client.isAuthenticated()) {
      this.user = (await this.#client.getUser()) ?? null;
      setTokenProvider(async () => (await this.#client?.getTokenSilently()) ?? null);
      await this.refreshMe();
    }
    this.ready = true;
  }

  async refreshMe(): Promise<void> {
    this.me = this.user ? await api.me() : null;
  }

  async login(): Promise<void> {
    await this.#client?.loginWithRedirect({ appState: { hash: window.location.hash } });
  }

  async logout(): Promise<void> {
    await this.#client?.logout({ logoutParams: { returnTo: window.location.origin } });
  }
}

export const auth = new AuthState();
