import React, { useState, useEffect } from 'react';
import { motion, useScroll, useTransform, AnimatePresence } from 'framer-motion';
import { 
  CarIcon, 
  MapPinIcon, 
  HeadphonesIcon, 
  StarIcon, 
  ArrowDownIcon,
  SunIcon,
  MoonIcon,
  PhoneIcon
} from '@heroicons/react/24/outline';

const PremiumWelcomePage = () => {
  const [darkMode, setDarkMode] = useState(true);
  const [showBookingButton, setShowBookingButton] = useState(false);
  const { scrollY } = useScroll();
  
  // Parallax transforms
  const heroY = useTransform(scrollY, [0, 500], [0, 150]);
  const heroOpacity = useTransform(scrollY, [0, 300], [1, 0]);
  const featuresY = useTransform(scrollY, [200, 800], [100, 0]);

  useEffect(() => {
    const handleScroll = () => {
      setShowBookingButton(window.scrollY > 400);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    document.documentElement.classList.toggle('dark');
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        duration: 0.8,
        staggerChildren: 0.2
      }
    }
  };

  const itemVariants = {
    hidden: { y: 50, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        duration: 0.6,
        ease: "easeOut"
      }
    }
  };

  const cardHoverVariants = {
    hover: {
      scale: 1.05,
      y: -10,
      transition: {
        duration: 0.3,
        ease: "easeOut"
      }
    }
  };

  return (
    <div className={`min-h-screen transition-colors duration-500 ${darkMode ? 'dark bg-gray-900' : 'bg-white'}`}>
      
      {/* Dark Mode Toggle */}
      <motion.button
        onClick={toggleDarkMode}
        className="fixed top-6 right-6 z-50 p-3 rounded-full bg-gradient-to-r from-amber-400 to-yellow-500 dark:from-indigo-600 dark:to-purple-600 shadow-lg hover:shadow-xl transition-all duration-300"
        whileHover={{ scale: 1.1, rotate: 180 }}
        whileTap={{ scale: 0.9 }}
        aria-label="Toggle dark mode"
      >
        {darkMode ? (
          <SunIcon className="w-6 h-6 text-white" />
        ) : (
          <MoonIcon className="w-6 h-6 text-white" />
        )}
      </motion.button>

      {/* Floating Book a Ride Button */}
      <AnimatePresence>
        {showBookingButton && (
          <motion.button
            initial={{ opacity: 0, y: 100 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 100 }}
            className="fixed bottom-6 right-6 z-50 px-6 py-3 bg-gradient-to-r from-amber-400 to-yellow-500 hover:from-amber-500 hover:to-yellow-600 text-black font-semibold rounded-full shadow-2xl hover:shadow-amber-500/25 transition-all duration-300"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => window.location.href = '/booking/new'}
          >
            <div className="flex items-center space-x-2">
              <CarIcon className="w-5 h-5" />
              <span>Book a Ride</span>
            </div>
          </motion.button>
        )}
      </AnimatePresence>

      {/* Hero Section */}
      <section className="relative h-screen flex items-center justify-center overflow-hidden">
        {/* Animated Background */}
        <div className="absolute inset-0">
          <motion.div
            style={{ y: heroY }}
            className="absolute inset-0 bg-gradient-to-br from-gray-900 via-black to-gray-800 dark:from-gray-900 dark:via-indigo-900 dark:to-purple-900"
          />
          
          {/* Moving Grid Animation */}
          <div className="absolute inset-0 opacity-20">
            <motion.div
              animate={{
                backgroundPosition: ['0% 0%', '100% 100%']
              }}
              transition={{
                duration: 20,
                repeat: Infinity,
                ease: "linear"
              }}
              className="w-full h-full bg-gradient-to-r from-transparent via-amber-500/10 to-transparent"
              style={{
                backgroundImage: `
                  linear-gradient(90deg, transparent 49%, rgba(255, 193, 7, 0.3) 50%, transparent 51%),
                  linear-gradient(0deg, transparent 49%, rgba(255, 193, 7, 0.3) 50%, transparent 51%)
                `,
                backgroundSize: '50px 50px'
              }}
            />
          </div>

          {/* Animated Car Silhouette */}
          <motion.div
            animate={{
              x: ['-100%', '100vw']
            }}
            transition={{
              duration: 15,
              repeat: Infinity,
              ease: "linear"
            }}
            className="absolute bottom-32 opacity-30"
          >
            <CarIcon className="w-16 h-16 text-amber-400" />
          </motion.div>
        </div>

        {/* Hero Content */}
        <motion.div
          style={{ opacity: heroOpacity }}
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="relative z-10 text-center px-4 max-w-4xl mx-auto"
        >
          <motion.h1
            variants={itemVariants}
            className="text-6xl md:text-8xl font-bold mb-6 bg-gradient-to-r from-white via-amber-200 to-amber-400 bg-clip-text text-transparent"
          >
            Welcome to CabEase
          </motion.h1>
          
          <motion.p
            variants={itemVariants}
            className="text-xl md:text-2xl text-gray-300 dark:text-gray-200 mb-12 leading-relaxed"
          >
            Your reliable and convenient cab booking service. 
            <br />
            Book a cab anytime, anywhere.
          </motion.p>

          <motion.div
            variants={itemVariants}
            className="flex flex-col sm:flex-row gap-6 justify-center items-center"
          >
            <motion.button
              whileHover={{ 
                scale: 1.05,
                boxShadow: "0 20px 40px rgba(255, 193, 7, 0.4)"
              }}
              whileTap={{ scale: 0.95 }}
              onClick={() => window.location.href = '/booking/new'}
              className="px-10 py-4 bg-gradient-to-r from-amber-400 to-yellow-500 hover:from-amber-500 hover:to-yellow-600 text-black font-bold rounded-full text-lg shadow-2xl hover:shadow-amber-500/25 transition-all duration-300"
            >
              Get Started
            </motion.button>
            
            <motion.button
              whileHover={{ 
                scale: 1.05,
                borderColor: "rgb(245, 158, 11)"
              }}
              whileTap={{ scale: 0.95 }}
              onClick={() => window.location.href = '/login'}
              className="px-10 py-4 border-2 border-gray-400 dark:border-gray-300 text-white hover:border-amber-500 hover:text-amber-400 font-semibold rounded-full text-lg transition-all duration-300"
            >
              Login
            </motion.button>
          </motion.div>
        </motion.div>

        {/* Scroll Indicator */}
        <motion.div
          animate={{ y: [0, 10, 0] }}
          transition={{ duration: 2, repeat: Infinity }}
          className="absolute bottom-8 left-1/2 transform -translate-x-1/2"
        >
          <ArrowDownIcon className="w-8 h-8 text-amber-400 opacity-80" />
        </motion.div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 relative overflow-hidden">
        <div className="max-w-7xl mx-auto">
          <motion.div
            style={{ y: featuresY }}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, margin: "-100px" }}
            variants={containerVariants}
          >
            <motion.h2
              variants={itemVariants}
              className="text-4xl md:text-6xl font-bold text-center mb-16 bg-gradient-to-r from-gray-800 to-gray-600 dark:from-white dark:to-gray-300 bg-clip-text text-transparent"
            >
              Premium Experience
            </motion.h2>

            <div className="grid md:grid-cols-3 gap-8">
              {/* Easy Booking Card */}
              <motion.div
                variants={itemVariants}
                whileHover="hover"
                className="group cursor-pointer"
              >
                <motion.div
                  variants={cardHoverVariants}
                  className="relative p-8 bg-white/10 dark:bg-white/5 backdrop-blur-lg rounded-3xl border border-white/20 shadow-xl hover:shadow-2xl transition-all duration-500"
                >
                  {/* Glowing background effect */}
                  <div className="absolute inset-0 bg-gradient-to-r from-amber-500/10 to-yellow-500/10 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                  
                  <div className="relative z-10">
                    <motion.div
                      whileHover={{ rotate: 360 }}
                      transition={{ duration: 0.5 }}
                      className="w-16 h-16 bg-gradient-to-r from-amber-400 to-yellow-500 rounded-2xl flex items-center justify-center mb-6 shadow-lg"
                    >
                      <CarIcon className="w-8 h-8 text-black" />
                    </motion.div>
                    
                    <h3 className="text-2xl font-bold text-gray-800 dark:text-white mb-4">
                      Easy Booking
                    </h3>
                    
                    <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                      Book your premium ride in seconds with our luxury interface. 
                      Experience the future of transportation.
                    </p>
                  </div>
                </motion.div>
              </motion.div>

              {/* GPS Precision Card */}
              <motion.div
                variants={itemVariants}
                whileHover="hover"
                className="group cursor-pointer"
              >
                <motion.div
                  variants={cardHoverVariants}
                  className="relative p-8 bg-white/10 dark:bg-white/5 backdrop-blur-lg rounded-3xl border border-white/20 shadow-xl hover:shadow-2xl transition-all duration-500"
                >
                  <div className="absolute inset-0 bg-gradient-to-r from-blue-500/10 to-indigo-500/10 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                  
                  <div className="relative z-10">
                    <motion.div
                      whileHover={{ rotate: 360 }}
                      transition={{ duration: 0.5 }}
                      className="w-16 h-16 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center mb-6 shadow-lg"
                    >
                      <MapPinIcon className="w-8 h-8 text-white" />
                    </motion.div>
                    
                    <h3 className="text-2xl font-bold text-gray-800 dark:text-white mb-4">
                      GPS Precision
                    </h3>
                    
                    <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                      Advanced GPS tracking with real-time updates. 
                      Know exactly where your luxury ride is at all times.
                    </p>
                  </div>
                </motion.div>
              </motion.div>

              {/* Premium Support Card */}
              <motion.div
                variants={itemVariants}
                whileHover="hover"
                className="group cursor-pointer"
              >
                <motion.div
                  variants={cardHoverVariants}
                  className="relative p-8 bg-white/10 dark:bg-white/5 backdrop-blur-lg rounded-3xl border border-white/20 shadow-xl hover:shadow-2xl transition-all duration-500"
                >
                  <div className="absolute inset-0 bg-gradient-to-r from-purple-500/10 to-pink-500/10 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                  
                  <div className="relative z-10">
                    <motion.div
                      whileHover={{ rotate: 360 }}
                      transition={{ duration: 0.5 }}
                      className="w-16 h-16 bg-gradient-to-r from-purple-500 to-pink-600 rounded-2xl flex items-center justify-center mb-6 shadow-lg"
                    >
                      <HeadphonesIcon className="w-8 h-8 text-white" />
                    </motion.div>
                    
                    <h3 className="text-2xl font-bold text-gray-800 dark:text-white mb-4">
                      Premium Support
                    </h3>
                    
                    <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                      Concierge-level support available 24/7. 
                      Experience white-glove service that matches our premium rides.
                    </p>
                  </div>
                </motion.div>
              </motion.div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section className="py-20 px-4 bg-gray-50 dark:bg-gray-800/50">
        <div className="max-w-6xl mx-auto">
          <motion.div
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            variants={containerVariants}
          >
            <motion.h2
              variants={itemVariants}
              className="text-4xl md:text-5xl font-bold text-center mb-16 text-gray-800 dark:text-white"
            >
              What Our Customers Say
            </motion.h2>

            <div className="grid md:grid-cols-2 gap-8">
              <motion.div
                variants={itemVariants}
                whileHover={{ scale: 1.02 }}
                className="p-8 bg-white dark:bg-gray-800 rounded-3xl shadow-lg hover:shadow-xl transition-all duration-300"
              >
                <div className="flex items-center mb-4">
                  {[...Array(5)].map((_, i) => (
                    <StarIcon key={i} className="w-5 h-5 text-yellow-400 fill-current" />
                  ))}
                </div>
                <p className="text-gray-600 dark:text-gray-300 mb-6 text-lg leading-relaxed italic">
                  "CabEase made my airport ride stress-free. Smooth booking and on-time pickup!"
                </p>
                <div className="flex items-center">
                  <div className="w-12 h-12 bg-gradient-to-r from-amber-400 to-yellow-500 rounded-full flex items-center justify-center text-black font-bold text-lg">
                    A
                  </div>
                  <div className="ml-4">
                    <p className="font-semibold text-gray-800 dark:text-white">Ananya S.</p>
                    <p className="text-gray-500 dark:text-gray-400 text-sm">Business Executive</p>
                  </div>
                </div>
              </motion.div>

              <motion.div
                variants={itemVariants}
                whileHover={{ scale: 1.02 }}
                className="p-8 bg-white dark:bg-gray-800 rounded-3xl shadow-lg hover:shadow-xl transition-all duration-300"
              >
                <div className="flex items-center mb-4">
                  {[...Array(5)].map((_, i) => (
                    <StarIcon key={i} className="w-5 h-5 text-yellow-400 fill-current" />
                  ))}
                </div>
                <p className="text-gray-600 dark:text-gray-300 mb-6 text-lg leading-relaxed italic">
                  "Premium service at its finest. The luxury vehicles and professional drivers exceed expectations."
                </p>
                <div className="flex items-center">
                  <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white font-bold text-lg">
                    R
                  </div>
                  <div className="ml-4">
                    <p className="font-semibold text-gray-800 dark:text-white">Rahul M.</p>
                    <p className="text-gray-500 dark:text-gray-400 text-sm">Entrepreneur</p>
                  </div>
                </div>
              </motion.div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 px-4 bg-gray-900 dark:bg-black">
        <div className="max-w-6xl mx-auto">
          <motion.div
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            variants={containerVariants}
            className="text-center"
          >
            <motion.div
              variants={itemVariants}
              className="flex flex-col md:flex-row justify-center items-center space-y-4 md:space-y-0 md:space-x-8 mb-8"
            >
              <a href="/privacy" className="text-gray-400 hover:text-amber-400 transition-colors duration-300">
                Privacy Policy
              </a>
              <a href="/terms" className="text-gray-400 hover:text-amber-400 transition-colors duration-300">
                Terms of Service
              </a>
              <a href="/contact" className="text-gray-400 hover:text-amber-400 transition-colors duration-300 flex items-center space-x-2">
                <PhoneIcon className="w-4 h-4" />
                <span>Contact Us</span>
              </a>
            </motion.div>
            
            <motion.p
              variants={itemVariants}
              className="text-gray-500 dark:text-gray-400"
            >
              CabEase © 2025. All rights reserved.
            </motion.p>
          </motion.div>
        </div>
      </footer>
    </div>
  );
};

export default PremiumWelcomePage;