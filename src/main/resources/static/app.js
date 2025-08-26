const API_BASE_URL = "";
const ENDPOINTS = {
  hello: "/api/hello",
  weather: "/api/weather",
  quote: "/api/quote",
};

/**
 * Sends a GET request to the hello endpoint with the provided name from the input element.
 * Handles loading state, parses JSON response, and displays success or error messages.
 *
 * @function
 * @returns {void}
 * @throws Will display error messages if DOM elements are not found, JSON parsing fails, or HTTP/connection errors occur.
 */
function loadGetMsg() {
  const nameElement = document.getElementById("name");
  const responseElement = document.getElementById("getrespmsg");

  if (!nameElement || !responseElement) {
    console.error("Elementos del DOM no encontrados");
    return;
  }

  const nameVar = nameElement.value.trim() || "World";
  const url = `${ENDPOINTS.hello}?name=${encodeURIComponent(nameVar)}`;

  showLoading(responseElement, "Enviando petición GET...");

  const xhttp = new XMLHttpRequest();

  xhttp.onload = function () {
    hideLoading(responseElement);

    if (this.status === 200) {
      try {
        const response = JSON.parse(this.responseText);
        displayResponse(responseElement, response, "success");
      } catch (e) {
        displayError(responseElement, "Error al parsear respuesta JSON");
      }
    } else {
      displayError(
        responseElement,
        `Error HTTP ${this.status}: ${this.statusText}`
      );
    }
  };

  xhttp.onerror = function () {
    hideLoading(responseElement);
    displayError(responseElement, "Error de conexión");
  };

  xhttp.ontimeout = function () {
    hideLoading(responseElement);
    displayError(responseElement, "Timeout de la petición");
  };

  xhttp.open("GET", url);
  xhttp.timeout = 10000;
  xhttp.send();
}

/**
 * Sends a POST request to the hello endpoint with the provided name from the input element.
 * Displays loading, success, or error messages in the response element.
 *
 * @function
 * @returns {void}
 */
function loadPostMsg() {
  const nameElement = document.getElementById("postname");
  const responseElement = document.getElementById("postrespmsg");

  if (!nameElement || !responseElement) {
    console.error("Elementos del DOM no encontrados");
    return;
  }

  const nameVar = nameElement.value.trim() || "World";
  const url = `${ENDPOINTS.hello}?name=${encodeURIComponent(nameVar)}`;

  showLoading(responseElement, "Enviando petición POST...");

  fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return response.json();
    })
    .then((data) => {
      hideLoading(responseElement);
      displayResponse(responseElement, data, "success");
    })
    .catch((error) => {
      hideLoading(responseElement);
      displayError(responseElement, error.message);
    });
}

/**
 * Fetches weather information from the configured endpoint and displays the result.
 * Shows a loading message while fetching, handles errors, and updates the UI accordingly.
 *
 * Relies on the following global functions:
 * - showLoading(element, message): Displays a loading message in the given element.
 * - hideLoading(element): Hides the loading message from the given element.
 * - displayWeatherResponse(element, data): Renders the weather data in the given element.
 * - displayError(element, message): Displays an error message in the given element.
 *
 * @function getWeather
 * @returns {void}
 */
function getWeather() {
  const responseElement = document.getElementById("weather-result");

  if (!responseElement) {
    console.error("Elemento de respuesta del clima no encontrado");
    return;
  }

  showLoading(responseElement, "Consultando información del clima...");

  fetch(ENDPOINTS.weather)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return response.json();
    })
    .then((data) => {
      hideLoading(responseElement);
      displayWeatherResponse(responseElement, data);
    })
    .catch((error) => {
      hideLoading(responseElement);
      displayError(responseElement, error.message);
    });
}

/**
 * Fetches an inspirational quote from the server and displays it in the UI.
 * Shows a loading message while fetching, and handles both success and error responses.
 *
 * @function
 * @returns {void}
 */
function getQuote() {
  const responseElement = document.getElementById("quote-result");

  if (!responseElement) {
    console.error("Elemento de respuesta de cita no encontrado");
    return;
  }

  showLoading(responseElement, "Obteniendo cita inspiradora...");

  fetch(ENDPOINTS.quote)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return response.json();
    })
    .then((data) => {
      hideLoading(responseElement);
      displayQuoteResponse(responseElement, data);
    })
    .catch((error) => {
      hideLoading(responseElement);
      displayError(responseElement, error.message);
    });
}

/**
 * Displays a loading message in the specified DOM element.
 *
 * @param {HTMLElement} element - The DOM element where the loading message will be shown.
 * @param {string} [message='Cargando...'] - The loading message to display. Defaults to 'Cargando...'.
 */
function showLoading(element, message = "Cargando...") {
  element.className = "response-area loading";
  element.textContent = message;
}

/**
 * Removes the 'loading' CSS class from the specified DOM element.
 *
 * @param {HTMLElement} element - The DOM element from which to remove the 'loading' class.
 */
function hideLoading(element) {
  element.classList.remove("loading");
}

/**
 * Displays a response message in the specified HTML element.
 *
 * Sets the element's class based on the response type and displays the data.
 * If the data is an object, it is stringified with indentation; otherwise, it is displayed as-is.
 *
 * @param {HTMLElement} element - The HTML element where the response will be displayed.
 * @param {Object|string} data - The response data to display. Can be an object or a string.
 * @param {string} [type='info'] - The type of response (e.g., 'info', 'error', 'success') used for styling.
 */
function displayResponse(element, data, type = "info") {
  element.className = `response-area ${type}`;

  if (typeof data === "object") {
    element.textContent = JSON.stringify(data, null, 2);
  } else {
    element.textContent = data;
  }
}

/**
 * Updates the specified DOM element to display weather information.
 *
 * @param {HTMLElement} element - The DOM element where the weather response will be displayed.
 * @param {Object} data - The weather data to display.
 * @param {string} [data.city] - The name of the city.
 * @param {string|number} [data.temperature] - The temperature value.
 * @param {string} [data.description] - A description of the weather.
 * @param {string|number} [data.humidity] - The humidity value.
 * @param {string} [data.message] - An optional message to display.
 */
function displayWeatherResponse(element, data) {
  element.className = "response-area success";

  element.innerHTML = `
<strong>🌤️ Información del Clima</strong>
Ciudad: ${data.city || "N/A"}
Temperatura: ${data.temperature || "N/A"}
Descripción: ${data.description || "N/A"}
Humedad: ${data.humidity || "N/A"}

${data.message ? "📝 " + data.message : ""}
    `.trim();
}

/**
 * Updates the given HTML element to display a formatted inspirational quote response.
 *
 * @param {HTMLElement} element - The DOM element where the quote will be displayed.
 * @param {Object} data - The data object containing quote information.
 * @param {string} [data.content] - The content of the quote.
 * @param {string} [data.author] - The author of the quote.
 * @param {string} [data.message] - An optional message to display below the quote.
 */
function displayQuoteResponse(element, data) {
  element.className = "response-area success";

  element.innerHTML = `
<strong>💭 Cita Inspiradora</strong>

"${data.content || "Contenido no disponible"}"
— ${data.author || "Autor desconocido"}

${data.message ? "📝 " + data.message : ""}
    `.trim();
}

/**
 * Displays an error message in the specified HTML element.
 *
 * Sets the element's class to 'response-area error' and updates its inner HTML
 * to show a formatted error message along with a suggestion to check the server connection.
 *
 * @param {HTMLElement} element - The DOM element where the error message will be displayed.
 * @param {string} message - The error message to display.
 */
function displayError(element, message) {
  element.className = "response-area error";
  element.innerHTML = `
<strong>❌ Error</strong>
${message}

Verifique la conexión con el servidor y vuelva a intentar.
    `.trim();
}

document.addEventListener("DOMContentLoaded", function () {
  console.log("📱 Aplicación web cargada correctamente");
  console.log("🌐 Servidor HTTP: http://localhost:35000");

  const nameInput = document.getElementById("name");
  const postnameInput = document.getElementById("postname");

  if (nameInput) {
    nameInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        loadGetMsg();
      }
    });
  }

  if (postnameInput) {
    postnameInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        loadPostMsg();
      }
    });
  }

  console.log("🔍 User Agent:", navigator.userAgent);
  console.log("📱 Platform:", navigator.platform);
});

window.addEventListener("error", function (e) {
  console.error("❌ Error global capturado:", e.error);
});

window.addEventListener("unhandledrejection", function (e) {
  console.error("❌ Promesa rechazada no manejada:", e.reason);
});

/**
 * Displays a notification message on the screen with a specified type.
 *
 * @param {string} message - The message to display in the notification.
 * @param {'info'|'error'} [type='info'] - The type of notification, which determines its color. Use 'error' for red and 'info' for green.
 */
function showNotification(message, type = "info") {
  const notification = document.createElement("div");
  notification.className = `notification notification-${type}`;
  notification.textContent = message;

  Object.assign(notification.style, {
    position: "fixed",
    top: "20px",
    right: "20px",
    padding: "1rem 1.5rem",
    borderRadius: "8px",
    background: type === "error" ? "#e74c3c" : "#27ae60",
    color: "white",
    fontWeight: "600",
    zIndex: "1000",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
    transform: "translateX(100%)",
    transition: "transform 0.3s ease",
  });

  document.body.appendChild(notification);

  setTimeout(() => {
    notification.style.transform = "translateX(0)";
  }, 100);

  setTimeout(() => {
    notification.style.transform = "translateX(100%)";
    setTimeout(() => {
      if (notification.parentNode) {
        notification.parentNode.removeChild(notification);
      }
    }, 300);
  }, 3000);
}
