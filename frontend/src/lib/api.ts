import type {
  ClientConfig,
  Me,
  MuscleGroup,
  Post,
  Thread,
  ThreadSummary,
  UserProfile,
  Workout,
  WorkoutInput,
} from './types';

/** Error thrown for non-2xx responses. Carries the RFC 9457 problem detail sent by the backend. */
export class ApiError extends Error {
  readonly status: number;
  readonly fieldErrors: Record<string, string>;

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

type TokenProvider = () => Promise<string | null>;

let tokenProvider: TokenProvider = async () => null;

/** Registers the function that supplies the access token (set by the auth module after login). */
export function setTokenProvider(provider: TokenProvider): void {
  tokenProvider = provider;
}

interface ProblemDetail {
  title?: string;
  detail?: string;
  errors?: Record<string, string>;
}

export async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  const token = await tokenProvider();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(path, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (!response.ok) {
    let problem: ProblemDetail = {};
    try {
      problem = (await response.json()) as ProblemDetail;
    } catch {
      // body was empty or not JSON
    }
    const message = problem.detail ?? problem.title ?? `Request failed (${response.status})`;
    throw new ApiError(response.status, message, problem.errors ?? {});
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

const get = <T>(path: string) => request<T>('GET', path);
const enc = encodeURIComponent;

export const api = {
  config: () => get<ClientConfig>('/api/config'),

  workouts: (muscleGroup?: MuscleGroup) =>
    get<Workout[]>(muscleGroup ? `/api/workouts?muscleGroup=${muscleGroup}` : '/api/workouts'),
  createWorkout: (input: WorkoutInput) => request<Workout>('POST', '/api/workouts', input),
  deleteWorkout: (id: string) => request<void>('DELETE', `/api/workouts/${enc(id)}`),

  me: () => get<Me>('/api/me'),
  plan: () => get<Workout[]>('/api/me/plan'),
  addToPlan: (workoutId: string) => request<Workout[]>('PUT', `/api/me/plan/${enc(workoutId)}`),
  removeFromPlan: (workoutId: string) => request<void>('DELETE', `/api/me/plan/${enc(workoutId)}`),

  threads: () => get<ThreadSummary[]>('/api/threads'),
  thread: (id: string) => get<Thread>(`/api/threads/${enc(id)}`),
  createThread: (title: string, content: string) =>
    request<Thread>('POST', '/api/threads', { title, content }),
  deleteThread: (id: string) => request<void>('DELETE', `/api/threads/${enc(id)}`),
  addPost: (threadId: string, content: string) =>
    request<Post>('POST', `/api/threads/${enc(threadId)}/posts`, { content }),
  editPost: (threadId: string, postId: string, content: string) =>
    request<Post>('PUT', `/api/threads/${enc(threadId)}/posts/${enc(postId)}`, { content }),
  deletePost: (threadId: string, postId: string) =>
    request<void>('DELETE', `/api/threads/${enc(threadId)}/posts/${enc(postId)}`),

  users: () => get<UserProfile[]>('/api/admin/users'),
  deleteUser: (id: string) => request<void>('DELETE', `/api/admin/users/${enc(id)}`),
};

/** Turns any thrown value into a message that can be shown to the user. */
export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.status === 401) return 'Please log in to do that.';
    if (error.status === 403) return 'You are not allowed to do that.';
    return error.message;
  }
  return 'Something went wrong. Is the backend running?';
}
