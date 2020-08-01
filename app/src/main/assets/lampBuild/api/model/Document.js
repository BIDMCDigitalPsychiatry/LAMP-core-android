"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var AccessCitation = /** @class */ (function () {
    function AccessCitation() {
        this.in = "";
        this.at = "";
        this.on = 0;
        this.by = "";
    }
    return AccessCitation;
}());
exports.AccessCitation = AccessCitation;
var Metadata = /** @class */ (function () {
    function Metadata() {
        this.access = new AccessCitation();
    }
    return Metadata;
}());
exports.Metadata = Metadata;
var Document = /** @class */ (function () {
    function Document() {
        this.meta = new Metadata();
        this.data = [];
    }
    return Document;
}());
exports.Document = Document;
var DurationInterval = /** @class */ (function () {
    function DurationInterval() {
    }
    return DurationInterval;
}());
exports.DurationInterval = DurationInterval;
var RepeatTypeLegacy;
(function (RepeatTypeLegacy) {
    RepeatTypeLegacy["hourly"] = "hourly";
    RepeatTypeLegacy["every3h"] = "every3h";
    RepeatTypeLegacy["every6h"] = "every6h";
    RepeatTypeLegacy["every12h"] = "every12h";
    RepeatTypeLegacy["daily"] = "daily";
    RepeatTypeLegacy["weekly"] = "weekly";
    RepeatTypeLegacy["biweekly"] = "biweekly";
    RepeatTypeLegacy["monthly"] = "monthly";
    RepeatTypeLegacy["bimonthly"] = "bimonthly";
    RepeatTypeLegacy["custom"] = "custom";
    RepeatTypeLegacy["none"] = "none";
})(RepeatTypeLegacy || (RepeatTypeLegacy = {}));
var DurationIntervalLegacy = /** @class */ (function () {
    function DurationIntervalLegacy() {
    }
    return DurationIntervalLegacy;
}());
exports.DurationIntervalLegacy = DurationIntervalLegacy;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiRG9jdW1lbnQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvbW9kZWwvRG9jdW1lbnQudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7QUFDQTtJQUFBO1FBQ1MsT0FBRSxHQUFHLEVBQUUsQ0FBQTtRQUNQLE9BQUUsR0FBRyxFQUFFLENBQUE7UUFDUCxPQUFFLEdBQWMsQ0FBQyxDQUFBO1FBQ2pCLE9BQUUsR0FBRyxFQUFFLENBQUE7SUFDaEIsQ0FBQztJQUFELHFCQUFDO0FBQUQsQ0FBQyxBQUxELElBS0M7QUFMWSx3Q0FBYztBQU0zQjtJQUFBO1FBQ1MsV0FBTSxHQUFtQixJQUFJLGNBQWMsRUFBRSxDQUFBO0lBQ3RELENBQUM7SUFBRCxlQUFDO0FBQUQsQ0FBQyxBQUZELElBRUM7QUFGWSw0QkFBUTtBQUdyQjtJQUFBO1FBQ1MsU0FBSSxHQUFhLElBQUksUUFBUSxFQUFFLENBQUE7UUFDL0IsU0FBSSxHQUFRLEVBQUUsQ0FBQTtJQUN2QixDQUFDO0lBQUQsZUFBQztBQUFELENBQUMsQUFIRCxJQUdDO0FBSFksNEJBQVE7QUFLckI7SUFBQTtJQUtBLENBQUM7SUFBRCx1QkFBQztBQUFELENBQUMsQUFMRCxJQUtDO0FBTFksNENBQWdCO0FBTTdCLElBQUssZ0JBWUo7QUFaRCxXQUFLLGdCQUFnQjtJQUNuQixxQ0FBaUIsQ0FBQTtJQUNqQix1Q0FBbUIsQ0FBQTtJQUNuQix1Q0FBbUIsQ0FBQTtJQUNuQix5Q0FBcUIsQ0FBQTtJQUNyQixtQ0FBZSxDQUFBO0lBQ2YscUNBQWlCLENBQUE7SUFDakIseUNBQXFCLENBQUE7SUFDckIsdUNBQW1CLENBQUE7SUFDbkIsMkNBQXVCLENBQUE7SUFDdkIscUNBQWlCLENBQUE7SUFDakIsaUNBQWEsQ0FBQTtBQUNmLENBQUMsRUFaSSxnQkFBZ0IsS0FBaEIsZ0JBQWdCLFFBWXBCO0FBQ0Q7SUFBQTtJQUlBLENBQUM7SUFBRCw2QkFBQztBQUFELENBQUMsQUFKRCxJQUlDO0FBSlksd0RBQXNCIn0=