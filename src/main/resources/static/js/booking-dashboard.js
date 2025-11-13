// CabEase Booking Dashboard JavaScript
// Enhanced with animations, interactions, and premium features

class BookingDashboard {
    constructor() {
        this.currentSection = 'booking';
        this.selectedRideType = 'economy';
        this.map = null;
        this.pickupMarker = null;
        this.dropMarker = null;
        this.driverMarker = null;
        this.routeLine = null;
        this.isSearching = false;
        this.searchTimeout = null;
        
        this.init();
    }
    
    init() {
        this.initializeMap();
        this.setupEventListeners();
        this.startAnimations();
        this.loadUserLocation();
        
        // Show booking section by default
        this.showSection('booking');
        
        console.log('🚗 CabEase Dashboard initialized');
    }
    
    initializeMap() {
        // Initialize Leaflet map
        this.map = L.map('map', {
            zoomControl: false,
            attributionControl: false
        }).setView([12.9716, 77.5946], 13); // Bangalore coordinates
        
        // Add dark theme tiles
        L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
            maxZoom: 18,
        }).addTo(this.map);
        
        // Add custom zoom control
        L.control.zoom({
            position: 'bottomright'
        }).addTo(this.map);
        
        // Add click handler for map
        this.map.on('click', (e) => {
            this.handleMapClick(e);
        });
        
        console.log('🗺️ Map initialized');
    }
    
    setupEventListeners() {
        // Profile dropdown toggle
        document.addEventListener('click', (e) => {
            const profileMenu = document.querySelector('.user-profile');
            const dropdown = document.getElementById('profileDropdown');
            
            if (profileMenu && profileMenu.contains(e.target)) {
                this.toggleProfileMenu();
            } else if (dropdown && !dropdown.contains(e.target)) {
                this.closeProfileMenu();
            }
        });
        
        // Location input handlers
        const pickupInput = document.getElementById('pickupLocation');
        const dropInput = document.getElementById('dropLocation');
        
        if (pickupInput) {
            pickupInput.addEventListener('input', (e) => this.handleLocationInput(e, 'pickup'));
            pickupInput.addEventListener('focus', (e) => this.handleLocationFocus(e, 'pickup'));
        }
        
        if (dropInput) {
            dropInput.addEventListener('input', (e) => this.handleLocationInput(e, 'drop'));
            dropInput.addEventListener('focus', (e) => this.handleLocationFocus(e, 'drop'));
        }
        
        // Keyboard shortcuts
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.handleEscapeKey();
            }
        });
        
        // Window resize handler
        window.addEventListener('resize', () => {
            this.handleResize();
        });
        
        console.log('🎯 Event listeners setup complete');
    }
    
    startAnimations() {
        // Animate navigation items
        this.animateNavItems();
        
        // Start ambient animations
        this.startAmbientAnimations();
        
        // Initialize intersection observer for scroll animations
        this.setupScrollAnimations();
    }
    
    animateNavItems() {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach((item, index) => {
            item.style.animationDelay = `${index * 0.1}s`;
            item.classList.add('animate-fade-in');
        });
    }
    
    startAmbientAnimations() {
        // Add floating particles effect
        this.createFloatingParticles();
        
        // Animate brand logo
        this.animateBrandLogo();
    }
    
    createFloatingParticles() {
        const particleCount = 20;
        const body = document.body;
        
        for (let i = 0; i < particleCount; i++) {
            const particle = document.createElement('div');
            particle.className = 'floating-particle';
            particle.style.cssText = `
                position: fixed;
                width: 4px;
                height: 4px;
                background: rgba(255, 107, 0, 0.3);
                border-radius: 50%;
                pointer-events: none;
                z-index: -1;
                left: ${Math.random() * 100}vw;
                top: ${Math.random() * 100}vh;
                animation: float ${3 + Math.random() * 4}s ease-in-out infinite;
                animation-delay: ${Math.random() * 2}s;
            `;
            body.appendChild(particle);
        }
    }
    
    animateBrandLogo() {
        const brandIcon = document.querySelector('.brand-icon');
        if (brandIcon) {
            setInterval(() => {
                brandIcon.style.transform = 'translateX(5px)';
                setTimeout(() => {
                    brandIcon.style.transform = 'translateX(0)';
                }, 200);
            }, 3000);
        }
    }
    
    setupScrollAnimations() {
        const observerOptions = {
            root: null,
            rootMargin: '0px',
            threshold: 0.1
        };
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-slide-up');
                }
            });
        }, observerOptions);
        
        // Observe animated elements
        document.querySelectorAll('.ride-type, .ride-item, .transaction-item, .support-option')
            .forEach(el => observer.observe(el));
    }
    
    loadUserLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    
                    this.map.setView([lat, lng], 15);
                    this.addPickupMarker(lat, lng);
                    
                    // Reverse geocode to get address
                    this.reverseGeocode(lat, lng, 'pickup');
                },
                (error) => {
                    console.warn('Location access denied:', error);
                    this.showToast('Location access denied. Using default location.', 'warning');
                }
            );
        }
    }
    
    reverseGeocode(lat, lng, type) {
        // Simple reverse geocoding (in production, use a proper service)
        const input = document.getElementById(type === 'pickup' ? 'pickupLocation' : 'dropLocation');
        if (input) {
            input.value = `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
        }
    }
    
    handleMapClick(e) {
        const { lat, lng } = e.latlng;
        
        const pickupInput = document.getElementById('pickupLocation');
        const dropInput = document.getElementById('dropLocation');
        
        if (!pickupInput.value) {
            this.addPickupMarker(lat, lng);
            this.reverseGeocode(lat, lng, 'pickup');
        } else if (!dropInput.value) {
            this.addDropMarker(lat, lng);
            this.reverseGeocode(lat, lng, 'drop');
            this.updateRoute();
        }
    }
    
    addPickupMarker(lat, lng) {
        if (this.pickupMarker) {
            this.map.removeLayer(this.pickupMarker);
        }
        
        this.pickupMarker = L.marker([lat, lng], {
            icon: this.createCustomIcon('🟢', '#27AE60')
        }).addTo(this.map);
        
        this.pickupMarker.bindPopup('Pickup Location').openPopup();
    }
    
    addDropMarker(lat, lng) {
        if (this.dropMarker) {
            this.map.removeLayer(this.dropMarker);
        }
        
        this.dropMarker = L.marker([lat, lng], {
            icon: this.createCustomIcon('🔴', '#E74C3C')
        }).addTo(this.map);
        
        this.dropMarker.bindPopup('Drop Location').openPopup();
    }
    
    createCustomIcon(emoji, color) {
        return L.divIcon({
            className: 'custom-div-icon',
            html: `<div style="
                background: ${color}; 
                border-radius: 50%; 
                width: 30px; 
                height: 30px; 
                display: flex; 
                align-items: center; 
                justify-content: center; 
                font-size: 16px;
                border: 3px solid white;
                box-shadow: 0 0 15px rgba(0,0,0,0.5);
            ">${emoji}</div>`,
            iconSize: [30, 30],
            iconAnchor: [15, 15]
        });
    }
    
    updateRoute() {
        if (this.pickupMarker && this.dropMarker) {
            if (this.routeLine) {
                this.map.removeLayer(this.routeLine);
            }
            
            const pickup = this.pickupMarker.getLatLng();
            const drop = this.dropMarker.getLatLng();
            
            this.routeLine = L.polyline([pickup, drop], {
                color: '#FF6B00',
                weight: 4,
                opacity: 0.8,
                dashArray: '10, 5'
            }).addTo(this.map);
            
            // Animate route drawing
            this.animateRoute();
            
            // Fit map to show both markers
            const group = new L.featureGroup([this.pickupMarker, this.dropMarker]);
            this.map.fitBounds(group.getBounds().pad(0.1));
            
            // Update ride estimates
            this.updateRideEstimates();
        }
    }
    
    animateRoute() {
        if (this.routeLine) {
            const path = this.routeLine.getElement();
            if (path) {
                const length = path.getTotalLength();
                path.style.strokeDasharray = length + ' ' + length;
                path.style.strokeDashoffset = length;
                path.style.animation = 'dash 2s ease-in-out forwards';
            }
        }
    }
    
    updateRideEstimates() {
        const pickup = this.pickupMarker?.getLatLng();
        const drop = this.dropMarker?.getLatLng();
        
        if (pickup && drop) {
            const distance = pickup.distanceTo(drop) / 1000; // Convert to km
            const baseTime = Math.max(5, Math.round(distance * 3)); // Rough estimate
            
            // Update prices based on distance
            const rideTypes = document.querySelectorAll('.ride-type');
            rideTypes.forEach((rideType, index) => {
                const priceElement = rideType.querySelector('.price');
                const etaElement = rideType.querySelector('.eta');
                
                if (priceElement && etaElement) {
                    const multipliers = [1, 1.3, 2]; // Economy, Premium, Luxury
                    const baseFare = Math.max(50, distance * 15);
                    const fare = Math.round(baseFare * multipliers[index]);
                    const eta = baseTime + (index * 2);
                    
                    priceElement.textContent = `₹${fare}`;
                    etaElement.textContent = `${eta} min away`;
                }
            });
            
            // Show ride types with animation
            const rideTypesContainer = document.getElementById('rideTypes');
            if (rideTypesContainer) {
                rideTypesContainer.style.opacity = '0';
                setTimeout(() => {
                    rideTypesContainer.style.opacity = '1';
                    rideTypesContainer.style.animation = 'slideUpFade 0.5s ease-out';
                }, 100);
            }
        }
    }
    
    handleLocationInput(event, type) {
        const value = event.target.value;
        
        // Simple auto-complete simulation
        if (value.length > 2) {
            this.showLocationSuggestions(value, type);
        } else {
            this.hideLocationSuggestions();
        }
    }
    
    handleLocationFocus(event, type) {
        event.target.style.transform = 'translateY(-2px)';
        event.target.style.boxShadow = '0 0 20px rgba(255, 107, 0, 0.3)';
    }
    
    showLocationSuggestions(query, type) {
        // Simulate location suggestions
        const suggestions = [
            'Bangalore International Airport',
            'MG Road Metro Station',
            'Brigade Road',
            'Koramangala',
            'Indiranagar',
            'Electronic City',
            'Whitefield',
            'JP Nagar'
        ].filter(location => 
            location.toLowerCase().includes(query.toLowerCase())
        );
        
        this.displaySuggestions(suggestions, type);
    }
    
    displaySuggestions(suggestions, type) {
        // Remove existing suggestions
        this.hideLocationSuggestions();
        
        if (suggestions.length === 0) return;
        
        const input = document.getElementById(type + 'Location');
        const suggestionsDiv = document.createElement('div');
        suggestionsDiv.className = 'location-suggestions';
        suggestionsDiv.style.cssText = `
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: rgba(13, 27, 42, 0.95);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 107, 0, 0.3);
            border-radius: 0 0 15px 15px;
            border-top: none;
            max-height: 200px;
            overflow-y: auto;
            z-index: 1000;
        `;
        
        suggestions.forEach(suggestion => {
            const item = document.createElement('div');
            item.className = 'suggestion-item';
            item.textContent = suggestion;
            item.style.cssText = `
                padding: 12px 15px;
                color: rgba(255, 255, 255, 0.8);
                cursor: pointer;
                transition: all 0.3s ease;
                border-bottom: 1px solid rgba(255, 255, 255, 0.05);
            `;
            
            item.addEventListener('mouseenter', () => {
                item.style.background = 'rgba(255, 107, 0, 0.1)';
                item.style.color = 'white';
            });
            
            item.addEventListener('mouseleave', () => {
                item.style.background = 'transparent';
                item.style.color = 'rgba(255, 255, 255, 0.8)';
            });
            
            item.addEventListener('click', () => {
                input.value = suggestion;
                this.hideLocationSuggestions();
                this.handleLocationSelection(suggestion, type);
            });
            
            suggestionsDiv.appendChild(item);
        });
        
        input.parentElement.style.position = 'relative';
        input.parentElement.appendChild(suggestionsDiv);
    }
    
    hideLocationSuggestions() {
        document.querySelectorAll('.location-suggestions').forEach(el => el.remove());
    }
    
    handleLocationSelection(location, type) {
        // Simulate coordinates for selected location
        const coordinates = this.getLocationCoordinates(location);
        
        if (type === 'pickup') {
            this.addPickupMarker(coordinates.lat, coordinates.lng);
        } else {
            this.addDropMarker(coordinates.lat, coordinates.lng);
        }
        
        if (this.pickupMarker && this.dropMarker) {
            this.updateRoute();
        }
    }
    
    getLocationCoordinates(location) {
        // Simulate location coordinates (in production, use geocoding service)
        const locations = {
            'Bangalore International Airport': { lat: 13.1986, lng: 77.7066 },
            'MG Road Metro Station': { lat: 12.9759, lng: 77.6061 },
            'Brigade Road': { lat: 12.9719, lng: 77.6037 },
            'Koramangala': { lat: 12.9279, lng: 77.6271 },
            'Indiranagar': { lat: 12.9784, lng: 77.6408 },
            'Electronic City': { lat: 12.8456, lng: 77.6603 },
            'Whitefield': { lat: 12.9698, lng: 77.7500 },
            'JP Nagar': { lat: 12.9082, lng: 77.5833 }
        };
        
        return locations[location] || { lat: 12.9716, lng: 77.5946 };
    }
    
    handleEscapeKey() {
        this.closeProfileMenu();
        this.hideLocationSuggestions();
        
        if (this.isSearching) {
            this.cancelSearch();
        }
    }
    
    handleResize() {
        if (this.map) {
            setTimeout(() => {
                this.map.invalidateSize();
            }, 100);
        }
    }
}

// Global functions for UI interactions
function toggleProfileMenu() {
    const dropdown = document.getElementById('profileDropdown');
    const arrow = document.querySelector('.profile-arrow');
    const profile = document.querySelector('.user-profile');
    
    if (dropdown && arrow && profile) {
        const isActive = dropdown.classList.contains('active');
        
        if (isActive) {
            dropdown.classList.remove('active');
            profile.classList.remove('active');
        } else {
            dropdown.classList.add('active');
            profile.classList.add('active');
        }
    }
}

function closeProfileMenu() {
    const dropdown = document.getElementById('profileDropdown');
    const profile = document.querySelector('.user-profile');
    
    if (dropdown && profile) {
        dropdown.classList.remove('active');
        profile.classList.remove('active');
    }
}

function showSection(sectionName) {
    // Hide all sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Show selected section
    const targetSection = document.getElementById(sectionName + '-section');
    if (targetSection) {
        targetSection.classList.add('active');
        
        // Update navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        
        const navItem = document.querySelector(`[data-section="${sectionName}"]`);
        if (navItem) {
            navItem.classList.add('active');
        }
        
        // Handle map resize for booking section
        if (sectionName === 'booking' && window.bookingDashboard?.map) {
            setTimeout(() => {
                window.bookingDashboard.map.invalidateSize();
            }, 300);
        }
    }
    
    console.log(`📱 Switched to ${sectionName} section`);
}

function selectRideType(type) {
    document.querySelectorAll('.ride-type').forEach(rideType => {
        rideType.classList.remove('active');
    });
    
    const selectedType = document.querySelector(`[data-type="${type}"]`);
    if (selectedType) {
        selectedType.classList.add('active');
        window.bookingDashboard.selectedRideType = type;
        
        // Add selection animation
        selectedType.style.transform = 'scale(1.02)';
        setTimeout(() => {
            selectedType.style.transform = '';
        }, 200);
    }
    
    console.log(`🚗 Selected ride type: ${type}`);
}

function getCurrentLocation() {
    if (navigator.geolocation) {
        const btn = document.querySelector('.current-location-btn');
        if (btn) {
            btn.style.transform = 'scale(0.9)';
            setTimeout(() => btn.style.transform = '', 150);
        }
        
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                
                if (window.bookingDashboard?.map) {
                    window.bookingDashboard.map.setView([lat, lng], 16);
                    window.bookingDashboard.addPickupMarker(lat, lng);
                    window.bookingDashboard.reverseGeocode(lat, lng, 'pickup');
                }
                
                showToast('Location updated successfully!', 'success');
            },
            () => {
                showToast('Unable to get your location', 'error');
            }
        );
    }
}

function setCurrentAsPickup() {
    getCurrentLocation();
}

function swapLocations() {
    const pickupInput = document.getElementById('pickupLocation');
    const dropInput = document.getElementById('dropLocation');
    
    if (pickupInput && dropInput) {
        const temp = pickupInput.value;
        pickupInput.value = dropInput.value;
        dropInput.value = temp;
        
        // Swap markers
        if (window.bookingDashboard?.pickupMarker && window.bookingDashboard?.dropMarker) {
            const pickupPos = window.bookingDashboard.pickupMarker.getLatLng();
            const dropPos = window.bookingDashboard.dropMarker.getLatLng();
            
            window.bookingDashboard.addPickupMarker(dropPos.lat, dropPos.lng);
            window.bookingDashboard.addDropMarker(pickupPos.lat, pickupPos.lng);
            window.bookingDashboard.updateRoute();
        }
        
        // Add swap animation
        const swapBtn = document.querySelector('.swap-locations');
        if (swapBtn) {
            swapBtn.style.transform = 'translateY(-50%) scale(1.2) rotate(180deg)';
            setTimeout(() => {
                swapBtn.style.transform = 'translateY(-50%)';
            }, 300);
        }
    }
}

function showScheduleOptions() {
    // Create schedule modal
    const modal = document.createElement('div');
    modal.className = 'schedule-modal';
    modal.innerHTML = `
        <div class="schedule-content">
            <h3>Schedule Your Ride</h3>
            <div class="schedule-form">
                <div class="form-group">
                    <label>Date</label>
                    <input type="date" class="schedule-input" min="${new Date().toISOString().split('T')[0]}">
                </div>
                <div class="form-group">
                    <label>Time</label>
                    <input type="time" class="schedule-input">
                </div>
                <div class="schedule-actions">
                    <button onclick="closeScheduleModal()" class="cancel-btn">Cancel</button>
                    <button onclick="confirmSchedule()" class="confirm-btn">Schedule Ride</button>
                </div>
            </div>
        </div>
    `;
    
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        backdrop-filter: blur(10px);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 3000;
        animation: fadeIn 0.3s ease-out;
    `;
    
    document.body.appendChild(modal);
}

function closeScheduleModal() {
    const modal = document.querySelector('.schedule-modal');
    if (modal) {
        modal.style.animation = 'fadeOut 0.3s ease-out';
        setTimeout(() => modal.remove(), 300);
    }
}

function bookRide() {
    const pickupInput = document.getElementById('pickupLocation');
    const dropInput = document.getElementById('dropLocation');
    
    if (!pickupInput?.value || !dropInput?.value) {
        showToast('Please select both pickup and drop locations', 'error');
        return;
    }
    
    if (!window.bookingDashboard.selectedRideType) {
        showToast('Please select a ride type', 'error');
        return;
    }
    
    // Start driver search
    showDriverSearch();
    
    console.log('🔍 Starting ride booking process');
}

function showDriverSearch() {
    const overlay = document.getElementById('driverSearchOverlay');
    if (overlay) {
        overlay.classList.add('active');
        window.bookingDashboard.isSearching = true;
        
        // Simulate search progress
        let progress = 0;
        const progressBar = document.querySelector('.progress-fill');
        const progressText = document.querySelector('.progress-text');
        
        const searchInterval = setInterval(() => {
            progress += Math.random() * 20;
            
            if (progressBar) {
                progressBar.style.width = Math.min(progress, 100) + '%';
            }
            
            if (progressText) {
                const messages = [
                    'Searching for nearby drivers...',
                    'Found 5 drivers nearby...',
                    'Connecting with best match...',
                    'Driver confirmed!'
                ];
                progressText.textContent = messages[Math.floor(progress / 25)] || messages[0];
            }
            
            if (progress >= 100) {
                clearInterval(searchInterval);
                setTimeout(() => {
                    hideDriverSearch();
                    showDriverFound();
                }, 500);
            }
        }, 800);
        
        // Auto-cancel after 30 seconds
        window.bookingDashboard.searchTimeout = setTimeout(() => {
            if (window.bookingDashboard.isSearching) {
                cancelSearch();
                showToast('No drivers available nearby. Please try again.', 'warning');
            }
        }, 30000);
    }
}

function hideDriverSearch() {
    const overlay = document.getElementById('driverSearchOverlay');
    if (overlay) {
        overlay.classList.remove('active');
        window.bookingDashboard.isSearching = false;
        
        if (window.bookingDashboard.searchTimeout) {
            clearTimeout(window.bookingDashboard.searchTimeout);
        }
    }
}

function cancelSearch() {
    hideDriverSearch();
    
    if (window.bookingDashboard.searchTimeout) {
        clearTimeout(window.bookingDashboard.searchTimeout);
    }
    
    showToast('Search cancelled', 'info');
    console.log('❌ Driver search cancelled');
}

function showDriverFound() {
    const overlay = document.getElementById('driverFoundOverlay');
    if (overlay) {
        overlay.classList.add('active');
        
        // Add driver marker to map
        if (window.bookingDashboard?.map && window.bookingDashboard?.pickupMarker) {
            const pickupPos = window.bookingDashboard.pickupMarker.getLatLng();
            
            // Place driver slightly away from pickup
            const driverLat = pickupPos.lat + (Math.random() - 0.5) * 0.01;
            const driverLng = pickupPos.lng + (Math.random() - 0.5) * 0.01;
            
            window.bookingDashboard.driverMarker = L.marker([driverLat, driverLng], {
                icon: window.bookingDashboard.createCustomIcon('🚗', '#FFD700')
            }).addTo(window.bookingDashboard.map);
            
            window.bookingDashboard.driverMarker.bindPopup('Your Driver - Rajesh Kumar').openPopup();
            
            // Animate driver movement towards pickup
            animateDriverMovement(driverLat, driverLng, pickupPos.lat, pickupPos.lng);
        }
        
        showToast('Driver found! Rajesh is on the way.', 'success');
        console.log('✅ Driver found and confirmed');
    }
}

function animateDriverMovement(startLat, startLng, endLat, endLng) {
    const steps = 50;
    const latStep = (endLat - startLat) / steps;
    const lngStep = (endLng - startLng) / steps;
    let currentStep = 0;
    
    const moveInterval = setInterval(() => {
        if (currentStep >= steps || !window.bookingDashboard?.driverMarker) {
            clearInterval(moveInterval);
            return;
        }
        
        currentStep++;
        const newLat = startLat + (latStep * currentStep);
        const newLng = startLng + (lngStep * currentStep);
        
        window.bookingDashboard.driverMarker.setLatLng([newLat, newLng]);
    }, 200);
}

function callDriver() {
    showToast('Calling Rajesh Kumar...', 'info');
    
    // Simulate call
    setTimeout(() => {
        showToast('Call connected! You can now talk to your driver.', 'success');
    }, 2000);
}

function messageDriver() {
    showToast('Message sent to driver', 'success');
    
    // In a real app, this would open a chat interface
    console.log('💬 Opening chat with driver');
}

function trackDriver() {
    showToast('Tracking driver location...', 'info');
    
    // Center map on driver
    if (window.bookingDashboard?.driverMarker && window.bookingDashboard?.map) {
        const driverPos = window.bookingDashboard.driverMarker.getLatLng();
        window.bookingDashboard.map.setView(driverPos, 16);
        
        // Pulse animation on driver marker
        const driverElement = window.bookingDashboard.driverMarker.getElement();
        if (driverElement) {
            driverElement.style.animation = 'pulse 1s ease-in-out 3 times';
        }
    }
}

function cancelRide() {
    const confirmed = confirm('Are you sure you want to cancel this ride? Cancellation charges may apply.');
    
    if (confirmed) {
        hideDriverFound();
        
        // Remove driver marker
        if (window.bookingDashboard?.driverMarker) {
            window.bookingDashboard.map.removeLayer(window.bookingDashboard.driverMarker);
            window.bookingDashboard.driverMarker = null;
        }
        
        showToast('Ride cancelled successfully', 'info');
        console.log('❌ Ride cancelled by user');
    }
}

function hideDriverFound() {
    const overlay = document.getElementById('driverFoundOverlay');
    if (overlay) {
        overlay.classList.remove('active');
    }
}

function filterRides(filter) {
    const filterTabs = document.querySelectorAll('.filter-tab');
    filterTabs.forEach(tab => tab.classList.remove('active'));
    
    const activeTab = document.querySelector(`[onclick="filterRides('${filter}')"]`);
    if (activeTab) {
        activeTab.classList.add('active');
    }
    
    // Filter ride items (simulation)
    const rideItems = document.querySelectorAll('.ride-item');
    rideItems.forEach((item, index) => {
        const isVisible = filter === 'all' || 
                         (filter === 'completed' && index % 3 !== 1) ||
                         (filter === 'cancelled' && index % 3 === 1);
        
        item.style.display = isVisible ? 'flex' : 'none';
    });
    
    console.log(`🔍 Filtered rides by: ${filter}`);
}

function addMoney() {
    const amount = prompt('Enter amount to add (₹):');
    if (amount && !isNaN(amount) && amount > 0) {
        showToast(`₹${amount} added to your wallet successfully!`, 'success');
        
        // Update balance display (simulation)
        const balanceElement = document.querySelector('.balance-amount');
        if (balanceElement) {
            const currentBalance = parseInt(balanceElement.textContent.replace('₹', '').replace(',', ''));
            const newBalance = currentBalance + parseInt(amount);
            balanceElement.textContent = `₹${newBalance.toLocaleString()}`;
            
            // Add animation
            balanceElement.style.transform = 'scale(1.1)';
            balanceElement.style.color = '#27AE60';
            setTimeout(() => {
                balanceElement.style.transform = '';
                balanceElement.style.color = '';
            }, 500);
        }
    }
}

function transferMoney() {
    showToast('Transfer feature coming soon!', 'info');
}

function showTransactionHistory() {
    showToast('Showing complete transaction history', 'info');
    // In a real app, this would show a detailed history page
}

function saveProfile() {
    const name = document.getElementById('profileName')?.value;
    const phone = document.getElementById('profilePhone')?.value;
    const address = document.getElementById('profileAddress')?.value;
    
    if (!name || !phone) {
        showToast('Please fill in all required fields', 'error');
        return;
    }
    
    // Simulate API call
    showToast('Saving profile...', 'info');
    
    setTimeout(() => {
        showToast('Profile updated successfully!', 'success');
    }, 1500);
    
    console.log('💾 Profile saved:', { name, phone, address });
}

function startLiveChat() {
    showToast('Connecting to support agent...', 'info');
    
    setTimeout(() => {
        showToast('Connected! You can now chat with our support team.', 'success');
    }, 2000);
}

function callSupport() {
    showToast('Calling support: +91 1800-123-4567', 'info');
}

function emailSupport() {
    showToast('Opening email client...', 'info');
    window.open('mailto:support@cabease.com?subject=Support Request');
}

function toggleFaq(faqItem) {
    const isActive = faqItem.classList.contains('active');
    
    // Close all FAQs
    document.querySelectorAll('.faq-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Open clicked FAQ if it wasn't active
    if (!isActive) {
        faqItem.classList.add('active');
    }
}

function showToast(message, type = 'info') {
    // Create toast notification
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-content">
            <span class="toast-icon">${getToastIcon(type)}</span>
            <span class="toast-message">${message}</span>
        </div>
    `;
    
    toast.style.cssText = `
        position: fixed;
        top: 90px;
        right: 20px;
        background: ${getToastColor(type)};
        color: white;
        padding: 15px 20px;
        border-radius: 10px;
        border: 1px solid ${getToastBorderColor(type)};
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        z-index: 4000;
        backdrop-filter: blur(10px);
        animation: slideInRight 0.4s ease-out;
        max-width: 350px;
        word-wrap: break-word;
    `;
    
    document.body.appendChild(toast);
    
    // Auto-remove after 4 seconds
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.4s ease-out';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
    
    console.log(`📱 Toast: ${type} - ${message}`);
}

function getToastIcon(type) {
    const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };
    return icons[type] || icons.info;
}

function getToastColor(type) {
    const colors = {
        success: 'linear-gradient(135deg, rgba(39, 174, 96, 0.9), rgba(46, 204, 113, 0.9))',
        error: 'linear-gradient(135deg, rgba(231, 76, 60, 0.9), rgba(192, 57, 43, 0.9))',
        warning: 'linear-gradient(135deg, rgba(243, 156, 18, 0.9), rgba(230, 126, 34, 0.9))',
        info: 'linear-gradient(135deg, rgba(52, 152, 219, 0.9), rgba(41, 128, 185, 0.9))'
    };
    return colors[type] || colors.info;
}

function getToastBorderColor(type) {
    const colors = {
        success: 'rgba(39, 174, 96, 0.5)',
        error: 'rgba(231, 76, 60, 0.5)',
        warning: 'rgba(243, 156, 18, 0.5)',
        info: 'rgba(52, 152, 219, 0.5)'
    };
    return colors[type] || colors.info;
}

function toggleMobileMenu() {
    const menu = document.querySelector('.navbar-collapse');
    const toggle = document.querySelector('.menu-toggle');
    
    if (menu && toggle) {
        menu.classList.toggle('show');
        toggle.classList.toggle('open');
    }
}

// Add custom CSS animations
const customAnimations = `
    <style>
        @keyframes float {
            0%, 100% { transform: translateY(0px); opacity: 0.3; }
            50% { transform: translateY(-20px); opacity: 0.8; }
        }
        
        @keyframes dash {
            0% { stroke-dashoffset: 1000; }
            100% { stroke-dashoffset: 0; }
        }
        
        @keyframes slideUpFade {
            from { transform: translateY(20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        
        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        
        @keyframes slideOutRight {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
        
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        @keyframes fadeOut {
            from { opacity: 1; }
            to { opacity: 0; }
        }
        
        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
        }
        
        .animate-fade-in {
            animation: fadeIn 0.6s ease-out forwards;
        }
        
        .animate-slide-up {
            animation: slideUpFade 0.6s ease-out forwards;
        }
        
        .schedule-content {
            background: linear-gradient(135deg, rgba(13, 27, 42, 0.95), rgba(47, 54, 64, 0.9));
            backdrop-filter: blur(20px);
            border: 2px solid rgba(255, 107, 0, 0.3);
            border-radius: 20px;
            padding: 30px;
            max-width: 400px;
            width: 90%;
        }
        
        .schedule-content h3 {
            color: white;
            text-align: center;
            margin-bottom: 25px;
            font-size: 1.4rem;
        }
        
        .schedule-form .form-group {
            margin-bottom: 20px;
        }
        
        .schedule-form label {
            display: block;
            color: white;
            margin-bottom: 8px;
            font-weight: 600;
        }
        
        .schedule-input {
            width: 100%;
            background: rgba(255, 255, 255, 0.1);
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 10px;
            padding: 12px 15px;
            color: white;
            font-size: 1rem;
        }
        
        .schedule-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 15px rgba(255, 107, 0, 0.3);
        }
        
        .schedule-actions {
            display: flex;
            gap: 15px;
            margin-top: 25px;
        }
        
        .cancel-btn, .confirm-btn {
            flex: 1;
            padding: 12px 20px;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            border: 2px solid;
        }
        
        .cancel-btn {
            background: rgba(231, 76, 60, 0.2);
            border-color: var(--danger);
            color: var(--danger);
        }
        
        .cancel-btn:hover {
            background: rgba(231, 76, 60, 0.3);
            transform: translateY(-2px);
        }
        
        .confirm-btn {
            background: var(--gradient-racing);
            border-color: var(--primary);
            color: white;
        }
        
        .confirm-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 107, 0, 0.3);
        }
        
        .toast-content {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .toast-icon {
            font-size: 1.2rem;
        }
        
        .toast-message {
            flex: 1;
            font-weight: 500;
        }
    </style>
`;

// Initialize the dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Add custom animations CSS
    document.head.insertAdjacentHTML('beforeend', customAnimations);
    
    // Initialize the booking dashboard
    window.bookingDashboard = new BookingDashboard();
    
    console.log('🚀 CabEase Booking Dashboard loaded successfully!');
});