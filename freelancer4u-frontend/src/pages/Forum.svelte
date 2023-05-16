<script>
    import axios from "axios";

    const api_root = window.location.origin;

    let forums = [];
    let forum = {
        id: null,
        creator: null,
    };

    function getForums() {
        var config = {
            method: "get",
            url: api_root + "/api/forum",
            headers: {},
        };

        axios(config)
            .then(function (response) {
                forums = response.data;
            })
            .catch(function (error) {
                alert("Could not get forums");
                console.log(error);
            });
    }
    getForums();

    function createForum() {
        var config = {
            method: "post",
            url: api_root + "/api/forum",
            headers: {
                "Content-Type": "application/json",
            },
            data: forum,
        };

        axios(config)
            .then(function (response) {
                alert("Forum created");
                getForums();
            })
            .catch(function (error) {
                alert("Could not create Forum");
                console.log(error);
            });
    }
</script>


<h1 class="mt-3">Create Forum</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="creator">Creator</label>
            <input
                bind:value={forum.creator}
                class="form-control"
                id="creator"
                type="text"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createForum}>Submit</button>
</form>

<h1>All Forums</h1>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Creator</th>
        </tr>
    </thead>
    <tbody>
        {#each forums as forum}
            <tr>
                <td>{forum.creator}</td>
            </tr>
        {/each}
    </tbody>
</table>