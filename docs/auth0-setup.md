# Auth0 setup

Without Auth0 the app runs in read-only demo mode. To enable login, roles and personal plans:

## 1. Create an API

**Applications → APIs → Create API**

- Name: `Bodybuilding API`
- Identifier (audience): `https://bodybuilding-api`

## 2. Create a Single Page Application

**Applications → Applications → Create Application → Single Page Web Applications**

In the settings add your URLs (for local development):

| Setting               | Value                                         |
| --------------------- | --------------------------------------------- |
| Allowed Callback URLs | `http://localhost:8080, http://localhost:5173` |
| Allowed Logout URLs   | `http://localhost:8080, http://localhost:5173` |
| Allowed Web Origins   | `http://localhost:8080, http://localhost:5173` |

Enable **Refresh Token Rotation** so the session survives page reloads.

## 3. Add name, e-mail and roles to the access token

Access tokens do not contain profile data by default. Create a post-login action
(**Actions → Library → Create Action → Login / Post Login**) and add it to the login flow:

```js
exports.onExecutePostLogin = async (event, api) => {
  const ns = 'https://bodybuilding.app/';
  const roles = event.authorization?.roles ?? [];

  api.accessToken.setCustomClaim(ns + 'roles', roles);
  api.accessToken.setCustomClaim(ns + 'name', event.user.nickname ?? event.user.name);
  api.accessToken.setCustomClaim(ns + 'email', event.user.email);
};
```

## 4. Create the admin role

**User Management → Roles → Create Role** with the name `admin` and assign it to your own user.
Admins can manage the workout library, moderate the forum and delete users.

## 5. Configure the app

Copy `.env.example` to `.env` and fill in the domain and client id of the SPA:

```bash
AUTH0_DOMAIN=your-tenant.eu.auth0.com
AUTH0_CLIENT_ID=...
AUTH0_AUDIENCE=https://bodybuilding-api
```

The backend serves these public values to the frontend at `GET /api/config`,
so the same Docker image works in every environment.
