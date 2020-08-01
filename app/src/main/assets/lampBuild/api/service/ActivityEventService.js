"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var express_1 = require("express");
var ActivityEventRepository_1 = require("../repository/ActivityEventRepository");
var ActivityEventRepository_2 = require("../repository/pouchRepository/ActivityEventRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.ActivityEventService = express_1.Router();
exports.ActivityEventService.post("/participant/:participant_id/activity_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, activity_event, outputLocal, _a, output, _b, e_1;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 5, , 6]);
                participant_id = req.params.participant_id;
                activity_event = req.body;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                return [4 /*yield*/, ActivityEventRepository_2.ActivityEventRepository._insert(participant_id, ae2re(req, [activity_event]))];
            case 1:
                outputLocal = (_a.data = _c.sent(), _a);
                res.json(outputLocal);
                return [3 /*break*/, 4];
            case 2:
                _b = {};
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._insert(participant_id, ae2re(req, [activity_event]))];
            case 3:
                output = (_b.data = _c.sent(), _b);
                res.json(output);
                _c.label = 4;
            case 4: return [3 /*break*/, 6];
            case 5:
                e_1 = _c.sent();
                if (e_1.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_1.message.split(".")[0]) || 500).json({ error: e_1.message });
                return [3 /*break*/, 6];
            case 6: return [2 /*return*/];
        }
    });
}); });
exports.ActivityEventService.delete("/participant/:participant_id/activity_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._delete(participant_id, origin, from, to)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_2 = _b.sent();
                if (e_2.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_2.message.split(".")[0]) || 500).json({ error: e_2.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ActivityEventService.get("/participant/:participant_id/activity_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, limit, outputLocal, _a, _b, _c, output, _d, _e, _f, e_3;
    var _g;
    return __generator(this, function (_h) {
        switch (_h.label) {
            case 0:
                _h.trys.push([0, 6, , 7]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_g = Number.parse(req.query.limit), (_g !== null && _g !== void 0 ? _g : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                if (!(process.env.LOCAL_DATA === "true")) return [3 /*break*/, 2];
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_2.ActivityEventRepository._select(participant_id, origin, from, to, limit)];
            case 1:
                outputLocal = (_a.data = _b.apply(void 0, _c.concat([_h.sent()])), _a);
                outputLocal = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(outputLocal) : outputLocal;
                res.json(outputLocal);
                return [3 /*break*/, 5];
            case 2: return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 3:
                participant_id = _h.sent();
                _d = {};
                _e = re2ae;
                _f = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(participant_id, origin, from, to, limit)];
            case 4:
                output = (_d.data = _e.apply(void 0, _f.concat([_h.sent()])), _d);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                _h.label = 5;
            case 5: return [3 /*break*/, 7];
            case 6:
                e_3 = _h.sent();
                if (e_3.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_3.message.split(".")[0]) || 500).json({ error: e_3.message });
                return [3 /*break*/, 7];
            case 7: return [2 /*return*/];
        }
    });
}); });
exports.ActivityEventService.get("/study/:study_id/activity_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, origin, from, to, limit, output, _a, _b, _c, e_4;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _e.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_d = Number.parse(req.query.limit), (_d !== null && _d !== void 0 ? _d : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _e.sent();
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(study_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _b.apply(void 0, _c.concat([_e.sent()])), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_4 = _e.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ActivityEventService.get("/researcher/:researcher_id/activity_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, origin, from, to, limit, output, _a, _b, _c, e_5;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _e.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_d = Number.parse(req.query.limit), (_d !== null && _d !== void 0 ? _d : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _e.sent();
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(researcher_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _b.apply(void 0, _c.concat([_e.sent()])), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_5 = _e.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
//////////////////////
//   [DEPRECATED]   //
//////////////////////
var ae2re = function (req, e) {
    if (req.path.endsWith("result_event"))
        // data: x.static_data, static_data: undefined,
        return e.map(function (x) { return (__assign(__assign({}, x), { temporal_slices: x.temporal_events, temporal_events: undefined })); });
    return e;
};
var re2ae = function (req, e) {
    if (req.path.endsWith("result_event"))
        // static_data: x.data, data: undefined,
        return e.map(function (x) { return (__assign(__assign({}, x), { temporal_events: x.temporal_slices, temporal_slices: undefined })); });
    return e;
};
exports.ResultEventService = express_1.Router();
exports.ResultEventService.post("/participant/:participant_id/result_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, activity_event, output, _a, e_6;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                activity_event = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._insert(participant_id, ae2re(req, [activity_event])[0])];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_6 = _b.sent();
                if (e_6.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_6.message.split(".")[0]) || 500).json({ error: e_6.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ResultEventService.delete("/participant/:participant_id/result_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, output, _a, e_7;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._delete(participant_id, origin, from, to)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_7 = _b.sent();
                if (e_7.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_7.message.split(".")[0]) || 500).json({ error: e_7.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ResultEventService.get("/participant/:participant_id/result_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, origin, from, to, limit, output, _a, _b, _c, e_8;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _e.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_d = Number.parse(req.query.limit), (_d !== null && _d !== void 0 ? _d : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _e.sent();
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(participant_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _b.apply(void 0, _c.concat([_e.sent()])), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_8 = _e.sent();
                if (e_8.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_8.message.split(".")[0]) || 500).json({ error: e_8.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ResultEventService.get("/study/:study_id/result_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, origin, from, to, limit, output, _a, _b, _c, e_9;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _e.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_d = Number.parse(req.query.limit), (_d !== null && _d !== void 0 ? _d : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _e.sent();
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(study_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _b.apply(void 0, _c.concat([_e.sent()])), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_9 = _e.sent();
                if (e_9.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_9.message.split(".")[0]) || 500).json({ error: e_9.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ResultEventService.get("/researcher/:researcher_id/result_event", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, origin, from, to, limit, output, _a, _b, _c, e_10;
    var _d;
    return __generator(this, function (_e) {
        switch (_e.label) {
            case 0:
                _e.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                origin = req.query.origin;
                from = Number.parse(req.query.from);
                to = Number.parse(req.query.to);
                limit = Math.min(Math.max((_d = Number.parse(req.query.limit), (_d !== null && _d !== void 0 ? _d : 1000)), -1000), 1000) // clamped to [-1000, 1000]
                ;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _e.sent();
                _a = {};
                _b = re2ae;
                _c = [req];
                return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(researcher_id, origin, from, to, limit)];
            case 2:
                output = (_a.data = _b.apply(void 0, _c.concat([_e.sent()])), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_10 = _e.sent();
                if (e_10.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_10.message.split(".")[0]) || 500).json({ error: e_10.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWN0aXZpdHlFdmVudFNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9BY3Rpdml0eUV2ZW50U2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsbUNBQW1EO0FBRW5ELGlGQUErRTtBQUUvRSxpR0FBc0g7QUFDdEgsdUNBQW9FO0FBQ3BFLG9EQUE2QjtBQUVoQixRQUFBLG9CQUFvQixHQUFHLGdCQUFNLEVBQUUsQ0FBQTtBQUM1Qyw0QkFBb0IsQ0FBQyxJQUFJLENBQUMsNkNBQTZDLEVBQUUsVUFBTyxHQUFZLEVBQUUsR0FBYTs7Ozs7O2dCQUVuRyxjQUFjLEdBQUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxjQUFjLENBQUE7Z0JBQ3hDLGNBQWMsR0FBRyxHQUFHLENBQUMsSUFBSSxDQUFBO3FCQVE1QixDQUFBLE9BQU8sQ0FBQyxHQUFHLENBQUMsVUFBVSxLQUFHLE1BQU0sQ0FBQSxFQUEvQix3QkFBK0I7O2dCQUNKLHFCQUFNLGlEQUFpQixDQUFDLE9BQU8sQ0FBQyxjQUFjLEVBQUUsS0FBSyxDQUFDLEdBQUcsRUFBRSxDQUFDLGNBQWMsQ0FBQyxDQUFDLENBQUMsRUFBQTs7Z0JBQW5HLFdBQVcsSUFBSyxPQUFJLEdBQUUsU0FBNkUsS0FBRTtnQkFDM0csR0FBRyxDQUFDLElBQUksQ0FBQyxXQUFXLENBQUMsQ0FBQTs7OztnQkFFQSxxQkFBTSxpREFBdUIsQ0FBQyxPQUFPLENBQUMsY0FBYyxFQUFFLEtBQUssQ0FBQyxHQUFHLEVBQUUsQ0FBQyxjQUFjLENBQUMsQ0FBQyxDQUFDLEVBQUE7O2dCQUFwRyxNQUFNLElBQUssT0FBSSxHQUFFLFNBQW1GLEtBQUU7Z0JBQzVHLEdBQUcsQ0FBQyxJQUFJLENBQUMsTUFBTSxDQUFDLENBQUE7Ozs7O2dCQUdoQixJQUFJLEdBQUMsQ0FBQyxPQUFPLEtBQUss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