const recipes = document.querySelectorAll(".recipe")
recipes.forEach(r => {
    const link = r.querySelector(".favorite")
    link.style.visibility = 'hidden'
    r.addEventListener('mouseover', function () {
        link.style.visibility = 'visible'
    })
    r.addEventListener('mouseout', function () {
        link.style.visibility = 'hidden'
    })
})