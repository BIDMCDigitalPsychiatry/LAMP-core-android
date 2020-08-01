"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var SensorName;
(function (SensorName) {
    SensorName["Analytics"] = "lamp.analytics";
    SensorName["Accelerometer"] = "lamp.accelerometer";
    SensorName["Bluetooth"] = "lamp.bluetooth";
    SensorName["Calls"] = "lamp.calls";
    SensorName["DeviceState"] = "lamp.device_state";
    SensorName["SMS"] = "lamp.sms";
    SensorName["WiFi"] = "lamp.wifi";
    SensorName["Audio"] = "lamp.audio_recordings";
    SensorName["Location"] = "lamp.gps";
    SensorName["ContextualLocation"] = "lamp.gps.contextual";
    SensorName["Height"] = "lamp.height";
    SensorName["Weight"] = "lamp.weight";
    SensorName["HeartRate"] = "lamp.heart_rate";
    SensorName["BloodPressure"] = "lamp.blood_pressure";
    SensorName["RespiratoryRate"] = "lamp.respiratory_rate";
    SensorName["Sleep"] = "lamp.sleep";
    SensorName["Steps"] = "lamp.steps";
    SensorName["Flights"] = "lamp.flights";
    SensorName["Segment"] = "lamp.segment";
    SensorName["Distance"] = "lamp.distance";
})(SensorName = exports.SensorName || (exports.SensorName = {}));
var LocationContext;
(function (LocationContext) {
    LocationContext["Home"] = "home";
    LocationContext["School"] = "school";
    LocationContext["Work"] = "work";
    LocationContext["Hospital"] = "hospital";
    LocationContext["Outside"] = "outside";
    LocationContext["Shopping"] = "shopping";
    LocationContext["Transit"] = "transit";
})(LocationContext = exports.LocationContext || (exports.LocationContext = {}));
var SocialContext;
(function (SocialContext) {
    SocialContext["Alone"] = "alone";
    SocialContext["Friends"] = "friends";
    SocialContext["Family"] = "family";
    SocialContext["Peers"] = "peers";
    SocialContext["Crowd"] = "crowd";
})(SocialContext = exports.SocialContext || (exports.SocialContext = {}));
var SensorEvent = /** @class */ (function () {
    function SensorEvent() {
    }
    return SensorEvent;
}());
exports.SensorEvent = SensorEvent;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2Vuc29yRXZlbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvbW9kZWwvU2Vuc29yRXZlbnQudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7QUFJQSxJQUFZLFVBcUJYO0FBckJELFdBQVksVUFBVTtJQUNwQiwwQ0FBNEIsQ0FBQTtJQUM1QixrREFBb0MsQ0FBQTtJQUNwQywwQ0FBNEIsQ0FBQTtJQUM1QixrQ0FBb0IsQ0FBQTtJQUNwQiwrQ0FBaUMsQ0FBQTtJQUNqQyw4QkFBZ0IsQ0FBQTtJQUNoQixnQ0FBa0IsQ0FBQTtJQUNsQiw2Q0FBK0IsQ0FBQTtJQUMvQixtQ0FBcUIsQ0FBQTtJQUNyQix3REFBMEMsQ0FBQTtJQUMxQyxvQ0FBc0IsQ0FBQTtJQUN0QixvQ0FBc0IsQ0FBQTtJQUN0QiwyQ0FBNkIsQ0FBQTtJQUM3QixtREFBcUMsQ0FBQTtJQUNyQyx1REFBeUMsQ0FBQTtJQUN6QyxrQ0FBb0IsQ0FBQTtJQUNwQixrQ0FBb0IsQ0FBQTtJQUNwQixzQ0FBd0IsQ0FBQTtJQUN4QixzQ0FBd0IsQ0FBQTtJQUN4Qix3Q0FBMEIsQ0FBQTtBQUM1QixDQUFDLEVBckJXLFVBQVUsR0FBVixrQkFBVSxLQUFWLGtCQUFVLFFBcUJyQjtBQUNELElBQVksZUFRWDtBQVJELFdBQVksZUFBZTtJQUN6QixnQ0FBYSxDQUFBO0lBQ2Isb0NBQWlCLENBQUE7SUFDakIsZ0NBQWEsQ0FBQTtJQUNiLHdDQUFxQixDQUFBO0lBQ3JCLHNDQUFtQixDQUFBO0lBQ25CLHdDQUFxQixDQUFBO0lBQ3JCLHNDQUFtQixDQUFBO0FBQ3JCLENBQUMsRUFSVyxlQUFlLEdBQWYsdUJBQWUsS0FBZix1QkFBZSxRQVExQjtBQUNELElBQVksYUFNWDtBQU5ELFdBQVksYUFBYTtJQUN2QixnQ0FBZSxDQUFBO0lBQ2Ysb0NBQW1CLENBQUE7SUFDbkIsa0NBQWlCLENBQUE7SUFDakIsZ0NBQWUsQ0FBQTtJQUNmLGdDQUFlLENBQUE7QUFDakIsQ0FBQyxFQU5XLGFBQWEsR0FBYixxQkFBYSxLQUFiLHFCQUFhLFFBTXhCO0FBQ0Q7SUFBQTtJQUlBLENBQUM7SUFBRCxrQkFBQztBQUFELENBQUMsQUFKRCxJQUlDO0FBSlksa0NBQVcifQ==