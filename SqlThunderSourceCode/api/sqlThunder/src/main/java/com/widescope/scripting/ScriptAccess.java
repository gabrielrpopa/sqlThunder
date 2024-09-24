package com.widescope.scripting;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class ScriptAccess implements RestInterface {
	private long  id;
	private long  userId;
	private long scriptId;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public long getScriptId() { return scriptId; }
	public void setScriptId(long scriptId) { this.scriptId = scriptId; }

	public ScriptAccess (final long id, final long userId, final long scriptId) {
		this.setId(id);
		this.setUserId(userId);
		this.setScriptId(scriptId);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
