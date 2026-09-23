const API_URL = "/recipes";

const CATEGORY_IMAGES = {
    STYRKA: "images/styrka.jpg",
    ATERHAMTNING: "images/aterhamtning.jpg",
    VIKTNEDGANG: "images/viktnedgang.jpg",
    UTHALLIGHET: "images/uthallighet.jpg"
};

const CATEGORY_LABELS = {
    STYRKA: "Styrka",
    ATERHAMTNING: "Återhämtning",
    VIKTNEDGANG: "Viktnedgång",
    UTHALLIGHET: "Uthållighet"
};

const CATEGORY_COLORS = {
    STYRKA: "#fb8c00",
    ATERHAMTNING: "#8e24aa",
    VIKTNEDGANG: "#43a047",
    UTHALLIGHET: "#f9a825"
};

const recipeForm = document.getElementById("recipe-form");
const recipeList = document.getElementById("recipe-list");
const formError = document.getElementById("form-error");

function loadRecipes() {
    fetch(API_URL)
        .then(response => response.json())
        .then(recipes => renderRecipes(recipes))
        .catch(error => console.error("Kunde inte hamta recept:", error));
}

function renderRecipes(recipes) {
    recipeList.innerHTML = "";

    recipes.forEach(recipe => {
        const card = createRecipeCard(recipe);
        recipeList.appendChild(card);
    });
}

function createRecipeCard(recipe) {
    const card = document.createElement("div");
    card.className = "recipe-card";

    const image = document.createElement("img");
    image.className = "card-image";
    image.src = CATEGORY_IMAGES[recipe.category];
    image.alt = recipe.title;

    const body = document.createElement("div");
    body.className = "card-body";

    const tag = document.createElement("span");
    tag.className = "category-tag";
    tag.style.background = CATEGORY_COLORS[recipe.category];
    tag.textContent = CATEGORY_LABELS[recipe.category];

    const title = document.createElement("h3");
    title.textContent = recipe.title;

    const description = document.createElement("p");
    description.className = "desc";
    description.textContent = recipe.description;

    const macros = document.createElement("div");
    macros.className = "macros";
    macros.innerHTML =
        "<span>🔥 " + recipe.calories + " kcal</span>" +
        "<span>💪 " + recipe.proteinGrams + " g protein</span>";

    const deleteButton = document.createElement("button");
    deleteButton.className = "delete-button";
    deleteButton.textContent = "Ta bort";
    deleteButton.addEventListener("click", () => deleteRecipe(recipe.id));

    body.appendChild(tag);
    body.appendChild(title);
    body.appendChild(description);
    body.appendChild(macros);
    body.appendChild(deleteButton);

    card.appendChild(image);
    card.appendChild(body);

    return card;
}

function deleteRecipe(id) {
    fetch(API_URL + "/" + id, { method: "DELETE" })
        .then(() => loadRecipes())
        .catch(error => console.error("Kunde inte ta bort recept:", error));
}

function handleFormSubmit(event) {
    event.preventDefault();
    formError.textContent = "";

    const newRecipe = {
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        category: document.getElementById("category").value,
        calories: Number(document.getElementById("calories").value),
        proteinGrams: Number(document.getElementById("proteinGrams").value),
        difficulty: document.getElementById("difficulty").value
    };

    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newRecipe)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Kunde inte spara receptet (status " + response.status + ")");
            }
            return response.json();
        })
        .then(() => {
            recipeForm.reset();
            loadRecipes();
        })
        .catch(error => {
            formError.textContent = error.message;
        });
}

recipeForm.addEventListener("submit", handleFormSubmit);

document.addEventListener("DOMContentLoaded", loadRecipes);