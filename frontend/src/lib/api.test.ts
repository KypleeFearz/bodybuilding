import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError, api, errorMessage, request, setTokenProvider } from './api';

const fetchMock = vi.fn<typeof fetch>();

beforeEach(() => {
  vi.stubGlobal('fetch', fetchMock);
  setTokenProvider(async () => null);
});

afterEach(() => {
  fetchMock.mockReset();
  vi.unstubAllGlobals();
});

const json = (status: number, body: unknown) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });

describe('request', () => {
  it('sends the access token when the user is logged in', async () => {
    setTokenProvider(async () => 'token-123');
    fetchMock.mockResolvedValue(json(200, []));

    await api.plan();

    const [, init] = fetchMock.mock.calls[0];
    expect((init?.headers as Record<string, string>).Authorization).toBe('Bearer token-123');
  });

  it('serializes the body as JSON', async () => {
    fetchMock.mockResolvedValue(json(201, { id: 't1' }));

    await api.createThread('Title', 'Hello');

    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/threads');
    expect(init?.method).toBe('POST');
    expect(JSON.parse(init?.body as string)).toEqual({ title: 'Title', content: 'Hello' });
  });

  it('returns undefined for 204 responses', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));

    await expect(request('DELETE', '/api/threads/1')).resolves.toBeUndefined();
  });

  it('turns problem details into an ApiError with field errors', async () => {
    fetchMock.mockResolvedValue(
      json(400, {
        title: 'Validation failed',
        detail: 'Invalid request content.',
        errors: { title: 'must not be blank' },
      }),
    );

    const error = await api.createThread('', 'x').catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).status).toBe(400);
    expect((error as ApiError).fieldErrors).toEqual({ title: 'must not be blank' });
  });

  it('encodes ids in the path', async () => {
    fetchMock.mockResolvedValue(json(200, {}));

    await api.thread('a/b');

    expect(fetchMock.mock.calls[0][0]).toBe('/api/threads/a%2Fb');
  });
});

describe('errorMessage', () => {
  it('gives friendly messages for auth errors', () => {
    expect(errorMessage(new ApiError(401, 'x'))).toBe('Please log in to do that.');
    expect(errorMessage(new ApiError(403, 'x'))).toBe('You are not allowed to do that.');
    expect(errorMessage(new ApiError(404, 'Thread not found'))).toBe('Thread not found');
    expect(errorMessage(new TypeError('fetch failed'))).toMatch(/backend/);
  });
});
