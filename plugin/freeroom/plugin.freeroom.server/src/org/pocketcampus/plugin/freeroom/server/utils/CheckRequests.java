package org.pocketcampus.plugin.freeroom.server.utils;

import java.net.HttpURLConnection;

import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteUserMessageReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteUserMessageRequest;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

/**
 * Checks that the request receive all well-formed and not corrupted (never
 * trust the user application!).
 * <p>
 * Also useful for debug to detect quickly client bugs.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class CheckRequests {
	public static FRReply checkFRRequest(FRRequest request) {
		FRReply reply = new FRReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "FRRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "FRRequest is null;";
		} else {
			if (!request.isSetPeriod() || request.getPeriod() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "FRPeriod is null;";
			}

			if (!request.isSetOnlyFreeRooms()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "boolean onlyFreeRooms is not set;";
			}

			if (!request.isSetUserGroup()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "User group is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	public static AutoCompleteReply checkAutoCompleteRequest(
			AutoCompleteRequest request) {
		AutoCompleteReply reply = new AutoCompleteReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "AutoCompleteRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "AutoCompleteRequest is null;";
		} else {
			if (!request.isSetConstraint() || request.getConstraint() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Constraint is null;";
			}

			if (!request.isSetUserGroup()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "User group is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	public static ImWorkingReply checkImWorkingRequest(ImWorkingRequest request) {
		ImWorkingReply reply = new ImWorkingReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "ImWorkingRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "ImWorkingRequest is null;";
		} else {
			if (!request.isSetWork() || request.getWork() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "work (WorkingOccupancy) is null;";
			}

			if (!request.isSetHash() || request.getHash() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Hash is not set;";
			}

			String workCheck = checkWorkingOccupancy(request.getWork());
			if (workCheck != null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += workCheck;
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	public static String checkWorkingOccupancy(WorkingOccupancy work) {
		boolean error = false;
		String comment = "WorkingOccupancy ";
		if (work == null) {
			comment += "is null;";
			error = true;
		}

		if (!work.isSetPeriod() || work.getPeriod() == null) {
			comment += "FRPeriod is not set;";
		}

		if (!work.isSetRoom() || work.getRoom() == null) {
			comment += "FRRoom is not set;";
		}

		if (error) {
			return comment;
		}
		return null;
	}

	public static WhoIsWorkingReply checkWhoIsWorkingRequest(
			WhoIsWorkingRequest request) {
		WhoIsWorkingReply reply = new WhoIsWorkingReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "WhoIsWorkingRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "WhoIsWorkingRequest is null;";
		} else {
			if (!request.isSetRoomUID() || request.getRoomUID() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "room ID  is null;";
			}

			if (!request.isSetPeriod() || request.getPeriod() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "FRPeriod is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	public static AutoCompleteUserMessageReply checkAutoCompleteUserMessageRequest(
			AutoCompleteUserMessageRequest request) {
		AutoCompleteUserMessageReply reply = new AutoCompleteUserMessageReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "AutoCompleteUserMessageRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "AutoCompleteUserMessageRequest is null;";
		} else {
			if (!request.isSetConstraint() || request.getConstraint() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Constraint is null;";
			}

			if (!request.isSetRoom() || request.getRoom() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Room is not set;";
			}

			if (!request.isSetPeriod() || request.getPeriod() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "FRPeriod is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}
}
