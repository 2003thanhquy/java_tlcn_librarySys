window.onload = function() {
    const ui = SwaggerUIBundle({
        url: "/api-docs",
        dom_id: '#swagger-ui',
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout",
        onComplete: function() {
            // Add custom logic to store multiple tokens
            const storedTokens = JSON.parse(localStorage.getItem('swaggerTokens')) || [];

            // Create dropdown for selecting token
            const tokenDropdown = document.createElement('select');
            tokenDropdown.id = 'tokenDropdown';
            tokenDropdown.style.margin = '10px';

            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.text = '-- Select a Token --';
            tokenDropdown.appendChild(defaultOption);

            storedTokens.forEach(token => {
                const option = document.createElement('option');
                option.value = token;
                option.text = token;
                tokenDropdown.appendChild(option);
            });

            document.querySelector('.topbar-wrapper').appendChild(tokenDropdown);

            tokenDropdown.addEventListener('change', function() {
                if (this.value) {
                    ui.preauthorizeApiKey("bearerAuth", this.value);
                }
            });

            // Add button for adding new token
            const addButton = document.createElement('button');
            addButton.textContent = 'Add Token';
            addButton.style.margin = '10px';
            document.querySelector('.topbar-wrapper').appendChild(addButton);

            addButton.addEventListener('click', function() {
                const token = prompt("Enter new token:");
                if (token) {
                    storedTokens.push(token);
                    localStorage.setItem('swaggerTokens', JSON.stringify(storedTokens));
                    const newOption = document.createElement('option');
                    newOption.value = token;
                    newOption.text = token;
                    tokenDropdown.appendChild(newOption);
                }
            });
        }
    });

    window.ui = ui;
}
