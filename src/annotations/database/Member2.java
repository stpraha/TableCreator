package annotations.database;

@DBTable(name = "MEMBER2")
public class Member2 {
	@SQLString() String hometown;
	@SQLString() String location;
	@SQLInteger() Integer wealth;
	@SQLInteger() Integer jobtime;
	@SQLString(value = 30, constraints = @Constraints(allowNull = false)) String handle;
	public String getHandle() { return handle;}
	public String gethometown() { return hometown;}
}
