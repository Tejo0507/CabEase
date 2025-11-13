package com.cabease.services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class GeminiService {
    @Value("${google.ai.api.key:dummy-key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    public String chat(String userMessage, Map<String, Object> context) {
        try {
            String contextualPrompt = buildContextualPrompt(userMessage, context);
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", contextualPrompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (!parts.isEmpty()) {
                        String aiResponse = (String) parts.get(0).get("text");
                        log.info("Gemini AI response: {}", aiResponse);
                        return aiResponse;
                    }
                }
            }
            return "Sorry, I couldn't generate a response right now. Please try again!";
        } catch (Exception e) {
            log.error("Error calling Gemini AI", e);
            return getFallbackResponse(userMessage, context);
        }
    }
    public String chat(String userMessage) {
        return chat(userMessage, null);
    }
    private String buildContextualPrompt(String userMessage, Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a friendly AI assistant for CabEase, a premium cab booking service in Chennai, India. ");
        prompt.append("You help users book rides, check fares, and answer questions about the service. ");
        prompt.append("Always be helpful, friendly, and professional. Use emojis appropriately. ");
        prompt.append("Keep responses concise but informative.\n\n");
        if (context != null) {
            if (context.get("pickupLocation") != null && !context.get("pickupLocation").toString().isEmpty()) {
                prompt.append("Current pickup location: ").append(context.get("pickupLocation")).append("\n");
            }
            if (context.get("dropLocation") != null && !context.get("dropLocation").toString().isEmpty()) {
                prompt.append("Current destination: ").append(context.get("dropLocation")).append("\n");
            }
            if (context.get("selectedVehicle") != null) {
                prompt.append("Selected vehicle: ").append(context.get("selectedVehicle")).append("\n");
            }
            if (context.get("currentRoute") != null) {
                Map<String, Object> route = (Map<String, Object>) context.get("currentRoute");
                if (route.get("distance") != null) {
                    prompt.append("Route distance: ").append(route.get("distance")).append(" km\n");
                }
                if (route.get("duration") != null) {
                    prompt.append("Route duration: ").append(route.get("duration")).append("\n");
                }
            }
        }
        prompt.append("\nService Information:\n");
        prompt.append("- Vehicle types: Mini (₹8/km), Sedan (₹12/km), SUV (₹18/km), Bike (₹5/km)\n");
        prompt.append("- Service area: Chennai and surrounding areas\n");
        prompt.append("- Peak hours: 6-10 AM and 5-9 PM (20% surge pricing)\n");
        prompt.append("- Base fare: ₹30 booking fee\n\n");
        prompt.append("User message: ").append(userMessage).append("\n\n");
        prompt.append("Provide a helpful response as CabEase AI assistant:");
        return prompt.toString();
    }
    private String getFallbackResponse(String userMessage, Map<String, Object> context) {
        String lowerMessage = userMessage.toLowerCase();
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("hey")) {
            return "Hello! 👋 I'm your CabEase AI assistant. I can help you book rides, check fares, or answer any questions about our service. How can I help you today?";
        }
        if (lowerMessage.contains("book") || lowerMessage.contains("ride") || lowerMessage.contains("cab") || lowerMessage.contains("taxi")) {
            if (context != null && context.get("pickupLocation") != null && context.get("dropLocation") != null) {
                return "Great! I can see you have both pickup and destination set. Would you like me to calculate the fare and help you book this ride? 🚗";
            } else {
                return "I'd be happy to help you book a ride! 🚗 Please enter your pickup location and destination in the booking form, and I'll help you get the best fare.";
            }
        }
        if (lowerMessage.contains("fare") || lowerMessage.contains("price") || lowerMessage.contains("cost")) {
            if (context != null && context.get("currentRoute") != null) {
                return "Based on your current route, here are our vehicle options:\n• Mini: ₹8/km (Economy)\n• Sedan: ₹12/km (Comfort)\n• SUV: ₹18/km (Premium)\n• Bike: ₹5/km (Fast)\n\nPlus ₹30 base fare. Would you like to book? 💰";
            } else {
                return "Our fare structure is:\n• Mini: ₹8/km 🚗\n• Sedan: ₹12/km 🚙\n• SUV: ₹18/km 🚐\n• Bike: ₹5/km 🏍️\n\nEnter your pickup and drop locations to get an exact fare estimate!";
            }
        }
        if (lowerMessage.contains("route") || lowerMessage.contains("distance") || lowerMessage.contains("time")) {
            if (context != null && context.get("currentRoute") != null) {
                Map<String, Object> route = (Map<String, Object>) context.get("currentRoute");
                return String.format("Your route is %s km and will take approximately %s. Ready to book? 🗺️",
                    route.get("distance"), route.get("duration"));
            } else {
                return "To check route details, please select your pickup and destination locations first, then click 'Show Route'. I'll provide distance and time estimates! 🗺️";
            }
        }
        if (lowerMessage.contains("vehicle") || lowerMessage.contains("car") || lowerMessage.contains("type")) {
            return "We offer several vehicle options:\n🚗 Mini - ₹8/km (4-seater, Economy)\n🚙 Sedan - ₹12/km (4-seater, Comfort)\n🚐 SUV - ₹18/km (6-seater, Premium)\n🏍️ Bike - ₹5/km (Fast delivery)\n\nWhich one would you prefer?";
        }
        if (lowerMessage.contains("contact") || lowerMessage.contains("support") || lowerMessage.contains("help")) {
            return "Need assistance? 🆘 You can contact our support team through the 'Contact Us' link, or I can help you right here! What specific help do you need?";
        }
        return "I'm here to help with your cab booking needs! 😊 You can ask me about:\n• Booking rides 🚗\n• Checking fares 💰\n• Route information 🗺️\n• Vehicle types 🚙\n• Our services 📞\n\nWhat would you like to know?";
    }
}
