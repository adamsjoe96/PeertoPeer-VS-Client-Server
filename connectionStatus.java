
public abstract class connectionStatus {

	public abstract Status getStatus();
	public abstract void setStatus(Status newTyp);
	public abstract TransferStatus getTransfer();
	public abstract void setTransfer(TransferStatus newTyp);
	
	public enum Status
	{
		Activ("Activ", 0),
		Inactiv("Inactiv", 1);
		
		private final int m_value;
		private final String m_name;
		
		Status(String name, int valu)
		{
			this.m_name = name;
			this.m_value = valu;
		}
		
		public int getValue()
		{
			return this.m_value;
		}
		public String getName()
		{
			return this.m_name;
		}
	}
	public enum TransferStatus
	{
		Keep_Alive("Alive", true),
		Keep_Close("Close", false);
		private final boolean m_value;
		private final String m_name;
		TransferStatus(String name, boolean value)
		{
			this.m_name = name;
			this.m_value = value;
		}
		
		public boolean getValue()
		{
			return this.m_value;
		}
		public String getName()
		{
			return this.m_name;
		}
	}
}
