const filterButtons = document.querySelectorAll('#filters > a')
filterButtons.forEach(function (button) {
    button.addEventListener('click', function (event) {
        event.preventDefault()
        const filterType = button.textContent.toLowerCase().trim()
        const isActive = button.classList.contains('active')
        if (isActive) {
            button.classList.remove('active')
            const params = new URLSearchParams()
            params.delete('filter')
            window.location.href = `?${params.toString()}`
        } else {
            const params = new URLSearchParams()
            params.set('filter', filterType)
            window.location.href = `?${params.toString()}`
        }
    })
})