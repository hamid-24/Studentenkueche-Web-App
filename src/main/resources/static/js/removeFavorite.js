const recipes = document.querySelectorAll('.recipe')
const noRecipesDiv = document.querySelector('.no')
const visible = document.querySelectorAll('.recipe:not(.d-none)')
if (visible.length === 0) {
    noRecipesDiv.style.display = 'block'
}
recipes.forEach(recipe => {
    const button = recipe.querySelector('.favorite')
    button.addEventListener('click', function (event) {
        event.preventDefault()
        const link = button.getAttribute('href')
        fetch(link)
            .then(response => {
                recipe.classList.add('d-none')
                const visibleRecipes=
                    document.querySelectorAll('.recipe:not(.d-none)')
                if (visibleRecipes.length === 0){
                    noRecipesDiv.style.display = 'block'
                }
            })
            .catch(error => {

            })
    })
})