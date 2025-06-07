// Enable Bootstrap tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Form validation
(function() {
    'use strict';
    window.addEventListener('load', function() {
        var forms = document.getElementsByClassName('needs-validation');
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();

// Password confirmation validation
function validatePassword() {
    var password = document.getElementById('password');
    var confirmPassword = document.getElementById('confirmPassword');
    if (password && confirmPassword) {
        if (password.value !== confirmPassword.value) {
            confirmPassword.setCustomValidity("Passwords don't match");
        } else {
            confirmPassword.setCustomValidity('');
        }
    }
}

// Location picker with Google Maps
function initLocationPicker() {
    var locationInput = document.getElementById('location');
    var latitudeInput = document.getElementById('latitude');
    var longitudeInput = document.getElementById('longitude');
    
    if (locationInput && latitudeInput && longitudeInput) {
        var autocomplete = new google.maps.places.Autocomplete(locationInput);
        autocomplete.addListener('place_changed', function() {
            var place = autocomplete.getPlace();
            if (place.geometry) {
                latitudeInput.value = place.geometry.location.lat();
                longitudeInput.value = place.geometry.location.lng();
            }
        });
    }
}

// Dynamic form fields
function addParticipantField() {
    var container = document.getElementById('participants-container');
    if (container) {
        var index = container.children.length;
        var template = document.getElementById('participant-template').innerHTML;
        var newField = template.replace(/\$\{index\}/g, index);
        container.insertAdjacentHTML('beforeend', newField);
    }
}

// AJAX form submission
function submitFormAsync(formElement, successCallback) {
    var form = formElement instanceof HTMLFormElement ? formElement : document.getElementById(formElement);
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            var formData = new FormData(form);
            
            fetch(form.action, {
                method: form.method,
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (successCallback) {
                    successCallback(data);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
}

// Infinite scroll for events list
let page = 0;
const loadMoreEvents = () => {
    const container = document.querySelector('.events-container');
    if (container) {
        fetch(`/api/events?page=${page}`)
            .then(response => response.json())
            .then(data => {
                if (data.length > 0) {
                    data.forEach(event => {
                        // Create and append event card
                        const eventCard = createEventCard(event);
                        container.appendChild(eventCard);
                    });
                    page++;
                }
            });
    }
};

// Helper function to create event card
function createEventCard(event) {
    const template = document.getElementById('event-card-template');
    if (template) {
        const card = template.content.cloneNode(true);
        // Fill in event details
        card.querySelector('.card-title').textContent = event.title;
        card.querySelector('.event-date').textContent = 
            new Date(event.dateTime).toLocaleDateString();
        card.querySelector('.event-location').textContent = event.location;
        return card;
    }
    return null;
}

// Intersection Observer for infinite scroll
const observeLastEvent = () => {
    const observer = new IntersectionObserver((entries) => {
        const lastEntry = entries[0];
        if (lastEntry.isIntersecting) {
            loadMoreEvents();
        }
    });

    const lastEvent = document.querySelector('.events-container .col-md-4:last-child');
    if (lastEvent) {
        observer.observe(lastEvent);
    }
}; 