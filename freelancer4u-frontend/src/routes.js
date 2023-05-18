import Home from "./pages/Home.svelte";
import Users from "./pages/Users.svelte";
import Forum from "./pages/Forum.svelte";
import Training from "./pages/Training.svelte";
import ForumDetail from "./pages/ForumDetail.svelte";

export default {
    '/': Home,
    '/home': Home,
    '/users': Users,
    '/forum': Forum,
    '/training': Training,
    '/forum/:id': ForumDetail,


}