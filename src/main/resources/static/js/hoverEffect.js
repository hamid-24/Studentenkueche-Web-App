const recipesWraps = document.querySelectorAll(".recipe")
recipesWraps.forEach(r => {
    const image = r.querySelector(".zoom")
    r.addEventListener('mouseover', function () {
        r.style.boxShadow = '0em 0em 1em 0em rgba(255, 255, 255, 0.1)'
        image.classList.add('transition')
    })
    r.addEventListener('mouseout', function () {
        r.style.boxShadow = '0 0 1em 0 rgba(0, 0, 0, 0.75)'
        image.classList.remove('transition')
    })
})