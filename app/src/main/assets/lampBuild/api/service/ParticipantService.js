"use strict";
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
var ParticipantRepository_1 = require("../repository/ParticipantRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.ParticipantService = express_1.Router();
exports.ParticipantService.post("/study/:study_id/participant", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, participant, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                participant = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._insert(study_id, participant)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_1 = _b.sent();
                if (e_1.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_1.message.split(".")[0]) || 500).json({ error: e_1.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ParticipantService.put("/participant/:participant_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, participant, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                participant = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._update(participant_id, participant)];
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
exports.ParticipantService.delete("/participant/:participant_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._delete(participant_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_3 = _b.sent();
                if (e_3.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_3.message.split(".")[0]) || 500).json({ error: e_3.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ParticipantService.get("/participant/:participant_id", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var participant_id, output, _a, e_4;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                participant_id = req.params.participant_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], participant_id)];
            case 1:
                participant_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select(participant_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_4 = _b.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ParticipantService.get("/study/:study_id/participant", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var study_id, output, _a, e_5;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                study_id = req.params.study_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], study_id)];
            case 1:
                study_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select(study_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_5 = _b.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.ParticipantService.get("/researcher/:researcher_id/participant", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var researcher_id, output, _a, e_6;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                researcher_id = req.params.researcher_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], researcher_id)];
            case 1:
                researcher_id = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select(researcher_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
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
exports.ParticipantService.get("/participant", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var _, output, _a, e_7;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), [])];
            case 1:
                _ = _b.sent();
                _a = {};
                return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select()];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUGFydGljaXBhbnRTZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3NlcnZpY2UvUGFydGljaXBhbnRTZXJ2aWNlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBQUEsbUNBQW1EO0FBRW5ELDZFQUEyRTtBQUMzRSx1Q0FBb0U7QUFDcEUsb0RBQTZCO0FBRWhCLFFBQUEsa0JBQWtCLEdBQUcsZ0JBQU0sRUFBRSxDQUFBO0FBQzFDLDBCQUFrQixDQUFDLElBQUksQ0FBQyw4QkFBOEIsRUFBRSxVQUFPLEdBQVksRUFBRSxHQUFhOzs7Ozs7Z0JBRWxGLFFBQVEsR0FBRyxHQUFHLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQTtnQkFDNUIsV0FBVyxHQUFHLEdBQUcsQ0FBQyxJQUFJLENBQUE7Z0JBQ2pCLHFCQUFNLGtCQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxlQUFlLENBQUMsRUFBRSxDQUFDLE1BQU0sRUFBRSxTQUFTLEVBQUUsUUFBUSxDQUFDLEVBQUUsUUFBUSxDQUFDLEVBQUE7O2dCQUEzRixRQUFRLEdBQUcsU0FBZ0YsQ0FBQTs7Z0JBQ3BFLHFCQUFNLDZDQUFxQixDQUFDLE9BQU8sQ0FBQyxRQUFRLEVBQUUsV0FBVyxDQUFDLEVBQUE7O2dCQUEzRSxNQUFNLElBQUssT0FBSSxHQUFFLFNBQTBELEtBQUU7Z0JBQ25GLEdBQUcsQ0FBQyxJQUFJLENBQUMsTUFBTSxDQUFDLENBQUE7Ozs7Z0JBRWhCLElBQUksR0FBQyxDQUFDLE9BQU8sS0FBSyx5QkFBeUI7b0JBQUUsR0FBRyxDQUFDLEdBQUcsQ0FBQyxrQkFBa0IsRUFBRSx3Q0FBb0MsQ0FBQyxDQUFBO2dCQUM5RyxHQUFHLENBQUMsTUFBTSxDQUFDLFFBQVEsQ0FBQyxHQUFDLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQyxJQUFJ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