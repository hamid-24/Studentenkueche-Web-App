const file = document.getElementById("file")
const img = document.getElementById("image")
file.addEventListener('change', function () {
    img.style.display = "inline"
    img.src = URL.createObjectURL(file.files[0])
})
const ingredientButton = document.getElementById("addIngredient")
const ingredientWrapper = document.getElementById("ingredientWrapper")
const inputTemplate = document.getElementById("inputTemplate")
ingredientButton.addEventListener("click", function () {
    const input = inputTemplate.cloneNode(true)
    input.querySelector("#ingredientName").value = ""
    const removeButton = input.querySelector("#removeIngredient")
    removeButton.style.display = "inline-block"
    removeButton.addEventListener("click", function () {
        input.remove()
    })
    ingredientWrapper.appendChild(input)
})
const textArea = document.getElementById("textarea")
const remaining = document.getElementById("remaining")
remaining.textContent = "remaining: " + textArea.maxLength
textArea.addEventListener("input", function () {
    const length = textArea.value.length
    const maxLength = textArea.maxLength
    const remainingChars = maxLength - length
    remaining.textContent = "remaining: " + remainingChars
})