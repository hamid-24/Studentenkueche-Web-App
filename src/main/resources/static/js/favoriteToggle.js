const favoriteButtons = document.querySelectorAll('.favorite')
favoriteButtons.forEach(button => {
    button.addEventListener('click', function (event) {
        event.preventDefault()
        const svg = this.querySelector('svg')
        const link = this.getAttribute('href')
        fetch(link)
            .then(response => {
                if (svg.style.fill === 'red') {
                    svg.style.fill = 'black'
                } else {
                    svg.style.fill = 'red'
                }
            })
            .catch(error => {

            })
    })
})