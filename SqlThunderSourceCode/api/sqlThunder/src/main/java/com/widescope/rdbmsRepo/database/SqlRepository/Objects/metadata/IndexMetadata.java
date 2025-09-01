package com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata;

public class IndexMetadata {
	private String INDEXFORM;
	private String COLUMN_NAME;
	private String INDEX_NAME;
	private boolean NON_UNIQUE;
	private short TYPE;
	private String ASC_OR_DESC;
	private int ORDINAL_POSITION;
	private int CARDINALITY;
	private short KEY_SEQ;
	
	public IndexMetadata() {
		this.setINDEXFORM("");
		this.setCOLUMN_NAME("");
		this.setINDEX_NAME("");
		this.setNON_UNIQUE(false);
		this.setTYPE((short)0);
		this.setASC_OR_DESC("");
		this.setORDINAL_POSITION(0);
		this.setCARDINALITY(0);
		this.setKEY_SEQ((short)0);
	}

	public String getINDEXFORM() { return INDEXFORM; }
	public void setINDEXFORM(String iNDEXFORM) { INDEXFORM = iNDEXFORM; }

	public String getCOLUMN_NAME() { return COLUMN_NAME; }
	public void setCOLUMN_NAME(String cOLUMN_NAME) { COLUMN_NAME = cOLUMN_NAME; }

	public String getINDEX_NAME() { return INDEX_NAME; }
	public void setINDEX_NAME(String iNDEX_NAME) { INDEX_NAME = iNDEX_NAME; }

	public short getTYPE() { return TYPE; }
	public void setTYPE(short tYPE) { TYPE = tYPE; }

	public String getASC_OR_DESC() { return ASC_OR_DESC; }
	public void setASC_OR_DESC(String aSC_OR_DESC) { ASC_OR_DESC = aSC_OR_DESC; }

	public int getORDINAL_POSITION() { return ORDINAL_POSITION; }
	public void setORDINAL_POSITION(int oRDINAL_POSITION) { ORDINAL_POSITION = oRDINAL_POSITION; }

	public boolean isNON_UNIQUE() { return NON_UNIQUE; }
	public void setNON_UNIQUE(boolean nON_UNIQUE) { NON_UNIQUE = nON_UNIQUE; }

	public int getCARDINALITY() { return CARDINALITY; }
	public void setCARDINALITY(int cARDINALITY) { CARDINALITY = cARDINALITY;}

	public short getKEY_SEQ() {	return KEY_SEQ;	}
	public void setKEY_SEQ(short kEY_SEQ) {	KEY_SEQ = kEY_SEQ; }
	
}
