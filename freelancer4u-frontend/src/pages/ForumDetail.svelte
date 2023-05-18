<script>
    import axios from "axios";
    import { jwt_token, user } from "../store";
    export let params = {};

    const api_root = window.location.origin;

    let forum = { creator: null, beitraege: [] };
    let beitrag = {
        text: null,
        creator: forum.creator,
        beitragCreator:null,
    };
    let forumId = params.id;
    let newText=null;
    function getForum() {
        var config = {
            method: "get",
            url: api_root + "/all/forum/" + forumId,
            headers: {},
        };

        axios(config)
            .then(function (response) {
                forum = response.data;
            })
            .catch(function (error) {
                alert("Could not get forums");
                console.log(error);
            });
    }
    getForum();

    function createBeitrag() {
        beitrag.creator = forum.creator;
        var config = {
            method: "put",
            url: api_root + "/api/service/createBeitrag",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: beitrag,
        };

        axios(config)
            .then(function (response) {
                alert("Forum created");
                getForum();
            })
            .catch(function (error) {
                alert("Could not create Forum");
                console.log(error);
            });
    }

    function editBeitrag(text, creator) {
        beitrag.creator = creator;
        beitrag.text = text;
        if(newText!==null && newText!==""){

        
        var config = {
            method: "put",
            url: api_root + "/api/service/editBeitrag?newText="+newText,
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: beitrag,
        };

        axios(config)
            .then(function (response) {
                alert("Forum edited");
                getForum();
            })
            .catch(function (error) {
                alert("Could not edit Forum");
                console.log(error);
            });
        }else{
            alert("Inputfeld darf nicht leer sein.");
        }
    }

    function deleteBeitrag(text, beitragCreator) {
        beitrag.creator = forum.creator;
        beitrag.text = text;
        beitrag.beitragCreator=beitragCreator;

        var config = {
            method: "put",
            url: api_root + "/api/service/deleteBeitrag",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: beitrag,
        };

        axios(config)
            .then(function (response) {
                alert("Forum deleted");
                getForum();
            })
            .catch(function (error) {
                alert("Could not delete Forum");
                console.log(error);
            });
    }
    function deleteBeitragAdmin(text, creator, user) {
        beitrag.beitragCreator = user;
        beitrag.creator=creator;
        beitrag.text = text;
        var config = {
            method: "put",
            url: api_root + "/api/service/deleteBeitrag",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: beitrag,
        };

        axios(config)
            .then(function (response) {
                alert("Forum deleted");
                getForum();
            })
            .catch(function (error) {
                alert("Could not delete Forum");
                console.log(error);
            });
    }
</script>

<h1 class="mt-3">Create Beitrag</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="creator">Text</label>
            <input
                bind:value={beitrag.text}
                class="form-control"
                id="text"
                type="text"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createBeitrag}
        >Submit</button
    >
</form>
<h1 class="mt-3">Edit Beitrag</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="creator">Text</label>
            <input
                bind:value={newText}
                class="form-control"
                id="newText"
                type="text"
            />
        </div>
    </div>
 
</form>
{#if forum.beitraege.length != 0}
    <h1>Forum</h1>
    <table class="table">
        <thead>
            <tr>
                <th scope="col">Name</th>
                <th scope="col">Text</th>
                <th scope="col">Actions</th>
            </tr>
        </thead>
        <tbody>
            {#each forum.beitraege as beitrag}
                <tr>
                    <td>{beitrag.user}</td>
                    <td>{beitrag.text}</td>
                    <td
                        >{#if $user.nickname === beitrag.user}
                            <button
                                type="button"
                                class="btn btn-primary btn-sm"
                                on:click={() => {
                                    deleteBeitrag(beitrag.text, $user.nickname);
                                }}
                            >
                                delete
                            </button>
                        {/if}
                        {#if $user.user_roles.includes("admin") && $user.nickname!== beitrag.user}
                            <button
                                type="button"
                                class="btn btn-primary btn-sm"
                                on:click={() => {
                                    deleteBeitragAdmin(beitrag.text, forum.creator, beitrag.user);
                                }}
                            >
                                delete
                            </button>
                        {/if}
                        {#if $user.nickname === beitrag.user}
                        <button
                            type="button"
                            class="btn btn-primary btn-sm"
                            on:click={() => {
                                editBeitrag(beitrag.text, forum.creator);
                            }}
                        >
                            edit
                        </button>
                    {/if}
                    </td>
                </tr>
            {/each}
        </tbody>
    </table>
{/if}
