// Enhanced UI Animations for CabEase - Premium Car Experience

document.addEventListener('DOMContentLoaded', function() {
    
    // Create dynamic car dashboard
    createCarDashboard();
    
    // Initialize car-themed particle system
    initCarParticles();
    
    // Add engine sound simulation (visual feedback only)
    addEngineEffects();

    // Navbar scroll effect
    const navbar = document.querySelector('.navbar');
    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Hamburger menu toggle
    const hamburger = document.querySelector('.hamburger');
    const navbarCollapse = document.querySelector('.navbar-collapse');
    hamburger.addEventListener('click', function() {
        this.classList.toggle('open');
        navbarCollapse.classList.toggle('show');
    });

    // Intersection Observer for scroll animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, observerOptions);

    // Observe elements
    document.querySelectorAll('.fade-in, .slide-in-left, .slide-in-right').forEach(el => {
        observer.observe(el);
    });

    // Hero text glitch effect
    const heroTitle = document.querySelector('.hero h1');
    if (heroTitle) {
        setInterval(() => {
            heroTitle.style.transform = `translate(${Math.random() * 4 - 2}px, ${Math.random() * 4 - 2}px)`;
            setTimeout(() => {
                heroTitle.style.transform = 'translate(0, 0)';
            }, 100);
        }, 3000);
    }

    // Floating icons animation
    document.querySelectorAll('.floating-icon').forEach(icon => {
        icon.style.animationDelay = Math.random() * 2 + 's';
    });

    // Morphing shapes
    document.querySelectorAll('.morphing-shape').forEach(shape => {
        shape.style.animationDelay = Math.random() * 6 + 's';
    });

    // Form interactions
    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('focus', function() {
            const icon = this.parentElement.querySelector('.input-icon');
            if (icon) icon.style.color = 'var(--primary)';
        });
        input.addEventListener('blur', function() {
            const icon = this.parentElement.querySelector('.input-icon');
            if (icon) icon.style.color = 'var(--primary)';
        });
    });

    // Button hover effects
    document.querySelectorAll('.btn-cta, .btn-submit').forEach(btn => {
        btn.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1) translateY(-5px)';
        });
        btn.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1) translateY(0)';
        });
    });

    // Social icons rotation
    document.querySelectorAll('.social-icon').forEach(icon => {
        icon.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.2) rotate(360deg)';
        });
        icon.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1) rotate(0deg)';
        });
    });

    // Card hover effects
    document.querySelectorAll('.card').forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-15px) scale(1.05)';
        });
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    // Animated map path
    const mapPath = document.querySelector('.map-path');
    if (mapPath) {
        mapPath.style.animation = 'drawPath 3s ease-in-out infinite';
    }

    // Particle effect in hero
    function createParticle() {
        const particle = document.createElement('div');
        particle.style.position = 'absolute';
        particle.style.width = '2px';
        particle.style.height = '2px';
        particle.style.background = 'rgba(255,255,255,0.5)';
        particle.style.borderRadius = '50%';
        particle.style.top = Math.random() * 100 + '%';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.animation = `floatParticle ${Math.random() * 10 + 5}s linear infinite`;
        document.querySelector('.hero').appendChild(particle);

        setTimeout(() => {
            particle.remove();
        }, 10000);
    }

    setInterval(createParticle, 500);

    // Canvas particle system
    const canvas = document.getElementById('particle-canvas');
    if (canvas) {
        const ctx = canvas.getContext('2d');
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        const particles = [];
        const particleCount = 100;

        class Particle {
            constructor() {
                this.x = Math.random() * canvas.width;
                this.y = Math.random() * canvas.height;
                this.size = Math.random() * 3 + 1;
                this.speedX = Math.random() * 2 - 1;
                this.speedY = Math.random() * 2 - 1;
                this.color = `rgba(${Math.random() * 100 + 155}, ${Math.random() * 100 + 126}, ${Math.random() * 100 + 234}, ${Math.random() * 0.5 + 0.5})`;
            }

            update() {
                this.x += this.speedX;
                this.y += this.speedY;

                if (this.x > canvas.width) this.x = 0;
                if (this.x < 0) this.x = canvas.width;
                if (this.y > canvas.height) this.y = 0;
                if (this.y < 0) this.y = canvas.height;
            }

            draw() {
                ctx.fillStyle = this.color;
                ctx.beginPath();
                ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
                ctx.fill();
            }
        }

        function initParticles() {
            for (let i = 0; i < particleCount; i++) {
                particles.push(new Particle());
            }
        }

        function animateParticles() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            particles.forEach(particle => {
                particle.update();
                particle.draw();
            });
            requestAnimationFrame(animateParticles);
        }

        initParticles();
        animateParticles();

        window.addEventListener('resize', () => {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        });
    }

    // Add CSS for particle animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes floatParticle {
            0% { transform: translateY(0) rotate(0deg); opacity: 1; }
            100% { transform: translateY(-100vh) rotate(360deg); opacity: 0; }
        }
        #particle-canvas {
            position: absolute;
            top: 0;
            left: 0;
            z-index: 1;
            pointer-events: none;
        }
        .engine-glow {
            animation: enginePulse 1s ease-in-out infinite;
        }
        @keyframes enginePulse {
            0%, 100% { box-shadow: 0 0 20px var(--primary); }
            50% { box-shadow: 0 0 40px var(--primary), 0 0 60px var(--accent); }
        }
        .speed-lines {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 1;
        }
        .speed-line {
            position: absolute;
            height: 2px;
            background: linear-gradient(90deg, transparent, var(--accent), transparent);
            animation: speedLine 0.3s linear forwards;
        }
        @keyframes speedLine {
            0% { width: 0; opacity: 1; }
            100% { width: 200px; opacity: 0; }
        }
    `;
    document.head.appendChild(style);

    // Car Dashboard Creation Function
    function createCarDashboard() {
        const dashboard = document.createElement('div');
        dashboard.innerHTML = `
            <div class="car-dashboard" style="
                position: fixed;
                bottom: 20px;
                right: 20px;
                background: var(--gradient-carbon);
                padding: 15px;
                border-radius: 15px;
                border: 2px solid var(--primary);
                box-shadow: 0 0 30px rgba(0, 0, 0, 0.8);
                z-index: 1000;
                display: none;
            ">
                <div style="display: flex; gap: 10px; align-items: center;">
                    <div class="speedometer"></div>
                    <div>
                        <div class="dashboard-light engine"></div>
                        <div class="dashboard-light oil"></div>
                        <div class="dashboard-light battery"></div>
                        <div class="dashboard-light fuel"></div>
                    </div>
                </div>
                <div style="color: var(--accent); font-size: 0.8rem; text-align: center; margin-top: 5px;">
                    Status: Ready
                </div>
            </div>
        `;
        document.body.appendChild(dashboard);

        // Show dashboard when booking form is active
        const bookingForms = document.querySelectorAll('form[action*="booking"]');
        bookingForms.forEach(form => {
            form.addEventListener('focus', () => {
                dashboard.querySelector('.car-dashboard').style.display = 'block';
            }, true);
        });
    }

    // Car Particles System
    function initCarParticles() {
        function createCarParticle() {
            const particle = document.createElement('div');
            particle.innerHTML = '🚗';
            particle.style.position = 'fixed';
            particle.style.fontSize = '20px';
            particle.style.color = 'rgba(255, 107, 0, 0.7)';
            particle.style.top = Math.random() * window.innerHeight + 'px';
            particle.style.left = '-30px';
            particle.style.zIndex = '1';
            particle.style.pointerEvents = 'none';
            particle.style.animation = `carDrive ${Math.random() * 5 + 3}s linear forwards`;
            
            document.body.appendChild(particle);
            
            setTimeout(() => particle.remove(), 8000);
        }

        // Add car particle animation
        const carDriveStyle = document.createElement('style');
        carDriveStyle.textContent = `
            @keyframes carDrive {
                0% { 
                    transform: translateX(0) rotate(0deg); 
                    opacity: 1; 
                }
                100% { 
                    transform: translateX(${window.innerWidth + 50}px) rotate(10deg); 
                    opacity: 0; 
                }
            }
        `;
        document.head.appendChild(carDriveStyle);

        // Create car particles periodically
        setInterval(createCarParticle, 8000);
    }

    // Engine Effects
    function addEngineEffects() {
        const buttons = document.querySelectorAll('.btn-cta, .btn-submit');
        buttons.forEach(btn => {
            btn.addEventListener('mouseenter', () => {
                btn.classList.add('engine-glow');
                createSpeedLines();
            });
            
            btn.addEventListener('mouseleave', () => {
                btn.classList.remove('engine-glow');
            });

            btn.addEventListener('click', () => {
                createEngineRevEffect();
            });
        });
    }

    // Speed Lines Effect
    function createSpeedLines() {
        const container = document.querySelector('.speed-lines') || createSpeedLinesContainer();
        
        for (let i = 0; i < 5; i++) {
            setTimeout(() => {
                const line = document.createElement('div');
                line.className = 'speed-line';
                line.style.top = Math.random() * window.innerHeight + 'px';
                line.style.left = Math.random() * (window.innerWidth - 200) + 'px';
                container.appendChild(line);
                
                setTimeout(() => line.remove(), 300);
            }, i * 50);
        }
    }

    function createSpeedLinesContainer() {
        const container = document.createElement('div');
        container.className = 'speed-lines';
        document.body.appendChild(container);
        return container;
    }

    // Engine Rev Effect
    function createEngineRevEffect() {
        const hero = document.querySelector('.hero');
        if (hero) {
            hero.style.animation = 'none';
            hero.style.transform = 'scale(1.02)';
            setTimeout(() => {
                hero.style.animation = '';
                hero.style.transform = '';
            }, 200);
        }
    }

    // Premium loading effect for page transitions
    function addLoadingEffect() {
        const loader = document.createElement('div');
        loader.innerHTML = `
            <div style="
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: var(--gradient-carbon);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
                animation: fadeOut 0.5s ease 2s forwards;
            ">
                <div class="car-icon" style="transform: scale(2);"></div>
                <div style="color: var(--accent); font-size: 1.5rem; margin-left: 30px; font-weight: bold;">
                    Starting Engine...
                </div>
            </div>
        `;
        document.body.appendChild(loader);
    }

    // Initialize loading effect
    addLoadingEffect();

});
