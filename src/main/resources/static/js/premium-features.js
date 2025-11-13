// Premium Interactive Features for CabEase
document.addEventListener('DOMContentLoaded', function() {
    
    // Initialize premium features
    initPremiumLoadingScreen();
    initPremiumNotifications();
    initAdvancedAnimations();
    initCarSoundEffects();
    initPremiumFormEnhancements();
    
    // Premium Loading Screen
    function initPremiumLoadingScreen() {
        // Show loading screen on page transitions
        const links = document.querySelectorAll('a:not([href^="#"]):not([href^="javascript:"]):not(.no-loading)');
        links.forEach(link => {
            link.addEventListener('click', function(e) {
                if (!this.target || this.target === '_self') {
                    showPremiumLoader();
                }
            });
        });
        
        // Hide loader when page is fully loaded
        window.addEventListener('load', function() {
            hidePremiumLoader();
        });
    }
    
    function showPremiumLoader() {
        const loader = document.createElement('div');
        loader.className = 'premium-loading';
        loader.innerHTML = `
            <div class="car-spinner"></div>
            <div class="loading-text">Starting Premium Engine...</div>
            <div class="progress-bar-premium">
                <div class="progress-fill"></div>
            </div>
            <div class="d-flex justify-content-center mt-3">
                <div class="dashboard-light engine"></div>
                <div class="dashboard-light fuel"></div>
                <div class="dashboard-light battery"></div>
            </div>
        `;
        document.body.appendChild(loader);
        
        // Update loading text
        const loadingTexts = [
            'Starting Premium Engine...',
            'Initializing Luxury Systems...',
            'Calibrating Elite Experience...',
            'Ready to Cruise...'
        ];
        
        let textIndex = 0;
        const textInterval = setInterval(() => {
            const textElement = loader.querySelector('.loading-text');
            if (textElement && textIndex < loadingTexts.length - 1) {
                textIndex++;
                textElement.textContent = loadingTexts[textIndex];
            } else {
                clearInterval(textInterval);
            }
        }, 800);
    }
    
    function hidePremiumLoader() {
        const loader = document.querySelector('.premium-loading');
        if (loader) {
            loader.classList.add('fade-out');
            setTimeout(() => loader.remove(), 500);
        }
    }
    
    // Premium Notifications System
    function initPremiumNotifications() {
        // Show welcome notification for new users
        setTimeout(() => {
            showPremiumNotification('Welcome to Elite Transportation!', 'premium', 'fas fa-crown');
        }, 2000);
        
        // Monitor form submissions
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', function() {
                showPremiumNotification('Processing your request...', 'info', 'fas fa-cog fa-spin');
            });
        });
    }
    
    function showPremiumNotification(message, type = 'info', icon = 'fas fa-info-circle') {
        const notification = document.createElement('div');
        notification.className = 'premium-notification';
        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="${icon} notification-icon"></i>
                <div>
                    <div style="font-weight: bold; color: var(--accent);">${message}</div>
                    <small style="color: rgba(255, 255, 255, 0.7);">CabEase Premium</small>
                </div>
                <button class="btn-close btn-close-white ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.style.animation = 'slideOutNotification 0.5s ease-in forwards';
                setTimeout(() => notification.remove(), 500);
            }
        }, 5000);
    }
    
    // Advanced Animations
    function initAdvancedAnimations() {
        // Engine rev effect on premium buttons
        const premiumButtons = document.querySelectorAll('.btn-cta, .btn-luxury, .btn-sport');
        premiumButtons.forEach(btn => {
            btn.addEventListener('mousedown', function() {
                this.classList.add('engine-rev');
                setTimeout(() => this.classList.remove('engine-rev'), 300);
            });
            
            btn.addEventListener('click', function() {
                this.classList.add('turbo-boost');
                setTimeout(() => this.classList.remove('turbo-boost'), 500);
            });
        });
        
        // Floating cars animation
        createFloatingCars();
        
        // Advanced hover effects for cards
        const cards = document.querySelectorAll('.card');
        cards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-20px) scale(1.05) rotateY(5deg)';
                this.style.boxShadow = '0 30px 60px rgba(0, 0, 0, 0.8), 0 0 50px var(--primary)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0) scale(1) rotateY(0deg)';
                this.style.boxShadow = '';
            });
        });
    }
    
    function createFloatingCars() {
        const carEmojis = ['🚗', '🚕', '🚙', '🏎️', '🚐'];
        
        setInterval(() => {
            const car = document.createElement('div');
            car.className = 'floating-car';
            car.textContent = carEmojis[Math.floor(Math.random() * carEmojis.length)];
            car.style.top = Math.random() * (window.innerHeight - 100) + 'px';
            car.style.animationDuration = (Math.random() * 5 + 8) + 's';
            
            document.body.appendChild(car);
            
            setTimeout(() => car.remove(), 15000);
        }, 12000);
    }
    
    // Car Sound Effects (Visual feedback)
    function initCarSoundEffects() {
        const soundButtons = document.querySelectorAll('.btn-cta, .btn-submit');
        soundButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                createSoundWave(this);
            });
        });
    }
    
    function createSoundWave(element) {
        const wave = document.createElement('div');
        wave.style.position = 'absolute';
        wave.style.width = '100px';
        wave.style.height = '100px';
        wave.style.border = '2px solid var(--primary)';
        wave.style.borderRadius = '50%';
        wave.style.top = '50%';
        wave.style.left = '50%';
        wave.style.transform = 'translate(-50%, -50%)';
        wave.style.animation = 'soundWave 0.6s ease-out forwards';
        wave.style.pointerEvents = 'none';
        wave.style.zIndex = '1000';
        
        element.style.position = 'relative';
        element.appendChild(wave);
        
        setTimeout(() => wave.remove(), 600);
        
        // Add sound wave animation
        if (!document.querySelector('#soundWaveStyle')) {
            const style = document.createElement('style');
            style.id = 'soundWaveStyle';
            style.textContent = `
                @keyframes soundWave {
                    0% {
                        transform: translate(-50%, -50%) scale(0);
                        opacity: 1;
                    }
                    100% {
                        transform: translate(-50%, -50%) scale(3);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);
        }
    }
    
    // Premium Form Enhancements
    function initPremiumFormEnhancements() {
        // Enhanced form validation
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                input.addEventListener('focus', function() {
                    this.classList.add('form-control-premium');
                    addInputGlow(this);
                });
                
                input.addEventListener('blur', function() {
                    removeInputGlow(this);
                });
                
                input.addEventListener('input', function() {
                    validateInputInRealTime(this);
                });
            });
        });
        
        // Premium autocomplete for location fields
        const locationInputs = document.querySelectorAll('#pickupLocation, #dropLocation');
        locationInputs.forEach(input => {
            input.addEventListener('change', function() {
                if (this.value) {
                    showLocationPreview(this.value);
                }
            });
        });
    }
    
    function addInputGlow(input) {
        input.style.boxShadow = '0 0 25px var(--primary)';
        input.style.borderColor = 'var(--accent)';
    }
    
    function removeInputGlow(input) {
        input.style.boxShadow = '';
        input.style.borderColor = '';
    }
    
    function validateInputInRealTime(input) {
        // Add real-time validation feedback
        if (input.checkValidity()) {
            input.style.borderColor = 'var(--success)';
            addValidationIcon(input, 'fas fa-check-circle', 'var(--success)');
        } else {
            input.style.borderColor = 'var(--danger)';
            addValidationIcon(input, 'fas fa-exclamation-circle', 'var(--danger)');
        }
    }
    
    function addValidationIcon(input, iconClass, color) {
        // Remove existing icon
        const existingIcon = input.parentElement.querySelector('.validation-icon');
        if (existingIcon) existingIcon.remove();
        
        // Add new icon
        const icon = document.createElement('i');
        icon.className = `${iconClass} validation-icon`;
        icon.style.position = 'absolute';
        icon.style.right = '40px';
        icon.style.top = '50%';
        icon.style.transform = 'translateY(-50%)';
        icon.style.color = color;
        icon.style.fontSize = '1.2rem';
        
        input.parentElement.appendChild(icon);
    }
    
    function showLocationPreview(location) {
        // Show location preview with map-like visual
        showPremiumNotification(`📍 Selected: ${location}`, 'success', 'fas fa-map-marker-alt');
    }
    
    // Premium Easter Eggs
    initEasterEggs();
    
    function initEasterEggs() {
        // Konami code for premium mode
        let konamiCode = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'KeyB', 'KeyA'];
        let userInput = [];
        
        document.addEventListener('keydown', function(e) {
            userInput.push(e.code);
            if (userInput.length > konamiCode.length) {
                userInput.shift();
            }
            
            if (JSON.stringify(userInput) === JSON.stringify(konamiCode)) {
                activateHyperMode();
                userInput = [];
            }
        });
        
        // Secret click sequence on logo
        const logo = document.querySelector('.navbar-brand');
        if (logo) {
            let clickCount = 0;
            logo.addEventListener('click', function(e) {
                e.preventDefault();
                clickCount++;
                if (clickCount === 5) {
                    activateRainbowMode();
                    clickCount = 0;
                }
                setTimeout(() => clickCount = 0, 2000);
            });
        }
    }
    
    function activateHyperMode() {
        document.body.style.filter = 'hue-rotate(180deg) saturate(1.5)';
        showPremiumNotification('🚀 HYPER MODE ACTIVATED!', 'premium', 'fas fa-rocket');
        
        setTimeout(() => {
            document.body.style.filter = '';
        }, 10000);
    }
    
    function activateRainbowMode() {
        document.body.style.animation = 'rainbow 2s linear infinite';
        showPremiumNotification('🌈 RAINBOW CRUISE MODE!', 'premium', 'fas fa-rainbow');
        
        const style = document.createElement('style');
        style.textContent = `
            @keyframes rainbow {
                0% { filter: hue-rotate(0deg); }
                100% { filter: hue-rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
        
        setTimeout(() => {
            document.body.style.animation = '';
            style.remove();
        }, 10000);
    }
    
    // Performance monitoring
    monitorPerformance();
    
    function monitorPerformance() {
        // Monitor page load performance
        window.addEventListener('load', function() {
            const loadTime = performance.now();
            if (loadTime > 3000) {
                console.log('🚗 CabEase: Optimizing for premium performance...');
            }
        });
    }
    
    // Initialize premium features on DOM ready
    console.log('🏎️ CabEase Premium Features Loaded Successfully!');
    
    // Add notification fade out animation
    const notificationStyle = document.createElement('style');
    notificationStyle.textContent = `
        @keyframes slideOutNotification {
            0% {
                transform: translateX(0);
                opacity: 1;
            }
            100% {
                transform: translateX(100%);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(notificationStyle);
});