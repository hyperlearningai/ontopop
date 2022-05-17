/**
 * Mapping API Integration Test
 *
 * Simple vanilla Javascript script designed to test browser requests
 * to the Mapping API, including CORS testing.
 *
 * @author jillurquddus
 * @since  2.0.0
 */

const form = document.getElementById('ontopop-form');
const url = 'https://<MAPPING API BASE URL>/mapping/map';
const api_key = '<API KEY VALUE>';

form.addEventListener('submit', e => {

    e.preventDefault();

    const request = new XMLHttpRequest();
    const files = document.querySelector('[name=file]').files;
    var formData = new FormData();
    formData.append("source", "rdf-xml");
    formData.append("target", "vis");
    formData.append("file", files[0]);

    request.open("POST", url);
    request.setRequestHeader("X-API-Key", api_key);
    request.send(formData);

    request.onerror = function() {
        console.log("Mapping API Error");
        console.log(request.responseText);
    }

    request.onreadystatechange = function() {
        if (request.readyState === 4) {
            console.log(request.getAllResponseHeaders());
            console.log(request.responseText);
        }
    }

});
