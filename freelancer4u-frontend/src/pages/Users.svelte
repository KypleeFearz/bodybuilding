<script>
    import axios from "axios";

    const api_root = window.location.origin;

    let users = [];
    let user = {
        id: null,
        email: null,
        name: null,
        age: null,
    };

    function getUsers() {
        var config = {
            method: "get",
            url: api_root + "/api/user",
            headers: {},
        };

        axios(config)
            .then(function (response) {
                users = response.data;
            })
            .catch(function (error) {
                alert("Could not get users");
                console.log(error);
            });
    }
    getUsers();

    function createUser() {
        var config = {
            method: "post",
            url: api_root + "/api/user",
            headers: {
                "Content-Type": "application/json",
            },
            data: user,
        };

        axios(config)
            .then(function (response) {
                alert("User created");
                getUsers();
            })
            .catch(function (error) {
                alert("Could not create User");
                console.log(error);
            });
    }
</script>


<h1 class="mt-3">Create User</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="description">Name</label>
            <input
                bind:value={user.name}
                class="form-control"
                id="name"
                type="text"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="email">E-Mail</label>
            <input
                bind:value={user.email}
                class="form-control"
                id="email"
                type="email"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="age">Age</label>
            <input
                bind:value={user.age}
                class="form-control"
                id="age"
                type="number"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createUser}>Submit</button>
</form>

<h1>All Users</h1>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">E-Mail</th>
            <th scope="col">Age</th>
        </tr>
    </thead>
    <tbody>
        {#each users as user}
            <tr>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.age}</td>
            </tr>
        {/each}
    </tbody>
</table>