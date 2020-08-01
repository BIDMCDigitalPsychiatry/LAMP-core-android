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
var TypeService_1 = require("./TypeService");
var CredentialService_1 = require("./CredentialService");
var ResearcherService_1 = require("./ResearcherService");
var StudyService_1 = require("./StudyService");
var ParticipantService_1 = require("./ParticipantService");
var ActivityService_1 = require("./ActivityService");
var ActivitySpecService_1 = require("./ActivitySpecService");
var ActivityEventService_1 = require("./ActivityEventService");
var SensorService_1 = require("./SensorService");
var SensorSpecService_1 = require("./SensorSpecService");
var SensorEventService_1 = require("./SensorEventService");
var jsonata_1 = __importDefault(require("jsonata"));
var Security_1 = require("../service/Security");
var ActivityEventRepository_1 = require("../repository/ActivityEventRepository");
var ActivityRepository_1 = require("../repository/ActivityRepository");
var CredentialRepository_1 = require("../repository/CredentialRepository");
var ParticipantRepository_1 = require("../repository/ParticipantRepository");
var SensorEventRepository_1 = require("../repository/SensorEventRepository");
var StudyRepository_1 = require("../repository/StudyRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
function Query(query, auth, verify) {
    if (verify === void 0) { verify = true; }
    return __awaiter(this, void 0, void 0, function () {
        var _this = this;
        return __generator(this, function (_a) {
            return [2 /*return*/, new Promise(function (resolve, reject) {
                    jsonata_1.default(query).evaluate({}, {
                        ActivityEvent_all: function (participant_id, origin, from, to) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], participant_id)];
                                    case 1:
                                        participant_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ActivityEventRepository_1.ActivityEventRepository._select(participant_id, origin, from, to)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Activity_all: function () { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["parent"])];
                                    case 1:
                                        _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ActivityRepository_1.ActivityRepository._select()];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Activity_view: function (participant_or_study_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], participant_or_study_id)];
                                    case 1:
                                        participant_or_study_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ActivityRepository_1.ActivityRepository._select(participant_or_study_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Credential_list: function (type_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "parent"], type_id)];
                                    case 1:
                                        type_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, CredentialRepository_1.CredentialRepository._select(type_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Participant_all: function (study_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], study_id)];
                                    case 1:
                                        study_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select(study_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Participant_view: function (participant_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], participant_id)];
                                    case 1:
                                        participant_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ParticipantRepository_1.ParticipantRepository._select(participant_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Researcher_all: function () { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, [])];
                                    case 1:
                                        _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._select()];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Researcher_view: function (researcher_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], researcher_id)];
                                    case 1:
                                        researcher_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, ResearcherRepository_1.ResearcherRepository._select(researcher_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        SensorEvent_all: function (participant_id, origin, from, to) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], participant_id)];
                                    case 1:
                                        participant_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, SensorEventRepository_1.SensorEventRepository._select(participant_id, origin, from, to)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Study_all: function (researcher_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], researcher_id)];
                                    case 1:
                                        researcher_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, StudyRepository_1.StudyRepository._select(researcher_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Study_view: function (study_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], study_id)];
                                    case 1:
                                        study_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, StudyRepository_1.StudyRepository._select(study_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Type_parent: function (type_id) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], type_id)];
                                    case 1:
                                        type_id = _a.sent();
                                        _a.label = 2;
                                    case 2: return [4 /*yield*/, TypeRepository_1.TypeRepository._parent(type_id)];
                                    case 3: return [2 /*return*/, _a.sent()];
                                }
                            });
                        }); },
                        Tags_list: function (type_id) { return __awaiter(_this, void 0, void 0, function () {
                            var _a, _b, _c;
                            return __generator(this, function (_d) {
                                switch (_d.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], type_id)];
                                    case 1:
                                        type_id = _d.sent();
                                        _d.label = 2;
                                    case 2:
                                        _b = (_a = []).concat;
                                        return [4 /*yield*/, TypeRepository_1.TypeRepository._list("a", type_id)];
                                    case 3:
                                        _c = [_d.sent()];
                                        return [4 /*yield*/, TypeRepository_1.TypeRepository._list("b", type_id)];
                                    case 4: return [2 /*return*/, _b.apply(_a, _c.concat([(_d.sent()).map(function (x) { return "dynamic/" + x; })]))];
                                }
                            });
                        }); },
                        Tags_view: function (type_id, attachment_key) { return __awaiter(_this, void 0, void 0, function () {
                            var x, e_1;
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!verify) return [3 /*break*/, 2];
                                        return [4 /*yield*/, Security_1._verify(auth, ["self", "sibling", "parent"], type_id)];
                                    case 1:
                                        type_id = _a.sent();
                                        _a.label = 2;
                                    case 2:
                                        x = null;
                                        _a.label = 3;
                                    case 3:
                                        _a.trys.push([3, 5, , 6]);
                                        return [4 /*yield*/, TypeRepository_1.TypeRepository._get("a", type_id, attachment_key)];
                                    case 4:
                                        x = _a.sent();
                                        return [3 /*break*/, 6];
                                    case 5:
                                        e_1 = _a.sent();
                                        return [3 /*break*/, 6];
                                    case 6: return [2 /*return*/, x];
                                }
                            });
                        }); },
                    }, function (error, result) {
                        if (error)
                            reject(error);
                        else
                            resolve(result);
                    });
                })];
        });
    });
}
exports.Query = Query;
var API = express_1.Router();
API.use(TypeService_1.TypeService);
API.use(CredentialService_1.CredentialService);
API.use(ResearcherService_1.ResearcherService);
API.use(StudyService_1.StudyService);
API.use(ParticipantService_1.ParticipantService);
API.use(ActivityService_1.ActivityService);
API.use(ActivitySpecService_1.ActivitySpecService);
API.use(ActivityEventService_1.ActivityEventService);
API.use(ActivityEventService_1.ResultEventService); // FIXME: DEPRECATED
API.use(SensorService_1.SensorService);
API.use(SensorSpecService_1.SensorSpecService);
API.use(SensorEventService_1.SensorEventService);
API.post("/", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var data, e_2;
    var _a;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 2, , 3]);
                return [4 /*yield*/, Query((_a = req.body, (_a !== null && _a !== void 0 ? _a : "")), req.get("Authorization"))];
            case 1:
                data = _b.sent();
                res.status(200).json(data);
                return [3 /*break*/, 3];
            case 2:
                e_2 = _b.sent();
                res.status(500).json({ error: e_2.message });
                return [3 /*break*/, 3];
            case 3: return [2 /*return*/];
        }
    });
}); });
exports.default = API;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaW5kZXguanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9pbmRleC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLG1DQUFtRDtBQUVuRCw2Q0FBMkM7QUFDM0MseURBQXVEO0FBQ3ZELHlEQUF1RDtBQUN2RCwrQ0FBNkM7QUFDN0MsMkRBQXlEO0FBQ3pELHFEQUFtRDtBQUNuRCw2REFBMkQ7QUFDM0QsK0RBQWlGO0FBQ2pGLGlEQUErQztBQUMvQyx5REFBdUQ7QUFDdkQsMkRBQXlEO0FBRXpELG9EQUE2QjtBQUM3QixnREFBNkM7QUFDN0MsaUZBQStFO0FBQy9FLHVFQUFxRTtBQUNyRSwyRUFBeUU7QUFDekUsNkVBQTJFO0FBQzNFLDZFQUEyRTtBQUMzRSxpRUFBK0Q7QUFDL0QsK0RBQTZEO0FBQzdELDJFQUF5RTtBQUV6RSxTQUFzQixLQUFLLENBQUMsS0FBYSxFQUFFLElBQXdCLEVBQUUsTUFBYTtJQUFiLHVCQUFBLEVBQUEsYUFBYTs7OztZQUNoRixzQkFBTyxJQUFJLE9BQU8sQ0FBQyxVQUFDLE9BQU8sRUFBRSxNQUFNO29CQUNqQyxpQkFBTyxDQUFDLEtBQUssQ0FBQyxDQUFDLFFBQVEsQ0FDckIsRUFBRSxFQUNGO3dCQUNFLGlCQUFpQixFQUFFLFVBQU8sY0FBc0IsRUFBRSxNQUFjLEVBQUUsSUFBWSxFQUFFLEVBQVU7Ozs7NkNBQ3BGLE1BQU0sRUFBTix3QkFBTTt3Q0FBbUIscUJBQU0sa0JBQU8sQ0FBQyxJQUFJLEVBQUUsQ0FBQyxNQUFNLEVBQUUsU0FBUyxFQUFFLFFBQVEsQ0FBQyxFQUFFLGNBQWMsQ0FBQyxFQUFBOzt3Q0FBbkYsY0FBYyxHQUFHLFNBQWtFLENBQUE7OzRDQUN4RixxQkFBTSxpREFBdUIsQ0FBQyxPQUFPLENBQUMsY0FBYyxFQUFFLE1BQU0sRUFBRSxJQUFJLEVBQUUsRUFBRSxDQUFDLEVBQUE7NENBQTlF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