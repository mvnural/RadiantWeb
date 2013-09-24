package ontology.similarity;

import static java.lang.System.out;

import java.util.HashSet;

public class TypeCompatibility {

	private static HashSet<String> types = new HashSet<String>();
	
	public enum dataType
	{
	  xsd_string, xsd_boolean, xsd_decimal, xsd_float, xsd_double,
	  xsd_integer, xsd_nonNegativeInteger, xsd_positiveInteger, xsd_nonPositiveInteger,
	  xsd_negativeInteger, xsd_long, xsd_int, xsd_short, xsd_byte, xsd_unsignedLong,
	  xsd_unsignedInt, xsd_unsignedShort, xsd_unsignedByte, xsd_hexBinary, xsd_base64Binary,
	  xsd_dateTime,	xsd_time, xsd_date,	xsd_gYearMonth, xsd_gYear, xsd_gMonthDay, xsd_gDay,	
	  xsd_gMonth, xsd_anyURI;
	
	  static int nTypes = dataType.values().length;
	  static double [][] matrix = new double [nTypes][nTypes];
	  static {
		  matrix[xsd_decimal.ordinal()][xsd_float.ordinal()] = .8;
		  matrix[xsd_decimal.ordinal()][xsd_double.ordinal()] = .9;
		  matrix[xsd_float.ordinal()][xsd_double.ordinal()] = .9;
		  matrix[xsd_integer.ordinal()][xsd_long.ordinal()] = .8;
		  matrix[xsd_integer.ordinal()][xsd_int.ordinal()] = .9;
		  matrix[xsd_integer.ordinal()][xsd_short.ordinal()] = .7;
		  matrix[xsd_integer.ordinal()][xsd_byte.ordinal()] = .6;
		  matrix[xsd_integer.ordinal()][xsd_hexBinary.ordinal()] = .9;
		  matrix[xsd_integer.ordinal()][xsd_base64Binary.ordinal()] = .9;
		  matrix[xsd_nonNegativeInteger.ordinal()][xsd_positiveInteger.ordinal()] = .9;
		  matrix[xsd_nonNegativeInteger.ordinal()][xsd_unsignedLong.ordinal()] = .8;
		  matrix[xsd_nonNegativeInteger.ordinal()][xsd_unsignedInt.ordinal()] = .9;
		  matrix[xsd_nonNegativeInteger.ordinal()][xsd_unsignedShort.ordinal()] = .7;
		  matrix[xsd_nonNegativeInteger.ordinal()][xsd_unsignedByte.ordinal()] = .6;
		  matrix[xsd_positiveInteger.ordinal()][xsd_unsignedLong.ordinal()] = .8;
		  matrix[xsd_positiveInteger.ordinal()][xsd_unsignedInt.ordinal()] = .9;
		  matrix[xsd_positiveInteger.ordinal()][xsd_unsignedShort.ordinal()] = .7;
		  matrix[xsd_positiveInteger.ordinal()][xsd_unsignedByte.ordinal()] = .6;
		  matrix[xsd_nonPositiveInteger.ordinal()][xsd_negativeInteger.ordinal()] = .9;
		  matrix[xsd_long.ordinal()][xsd_int.ordinal()] = .8;
		  matrix[xsd_long.ordinal()][xsd_short.ordinal()] = .7;
		  matrix[xsd_long.ordinal()][xsd_byte.ordinal()] = .6;
		  matrix[xsd_long.ordinal()][xsd_hexBinary.ordinal()] = .9;
		  matrix[xsd_long.ordinal()][xsd_base64Binary.ordinal()] = .9;
		  matrix[xsd_int.ordinal()][xsd_short.ordinal()] = .8;
		  matrix[xsd_int.ordinal()][xsd_byte.ordinal()] = .7;
		  matrix[xsd_int.ordinal()][xsd_hexBinary.ordinal()] = .9;
		  matrix[xsd_int.ordinal()][xsd_base64Binary.ordinal()] = .9;
		  matrix[xsd_short.ordinal()][xsd_byte.ordinal()] = .8;
		  matrix[xsd_short.ordinal()][xsd_hexBinary.ordinal()] = .8;
		  matrix[xsd_short.ordinal()][xsd_base64Binary.ordinal()] = .8;
		  matrix[xsd_byte.ordinal()][xsd_hexBinary.ordinal()] = .7;
		  matrix[xsd_byte.ordinal()][xsd_base64Binary.ordinal()] = .7;
		  matrix[xsd_unsignedLong.ordinal()][xsd_unsignedInt.ordinal()] = .8;
		  matrix[xsd_unsignedLong.ordinal()][xsd_unsignedShort.ordinal()] = .7;
		  matrix[xsd_unsignedLong.ordinal()][xsd_unsignedByte.ordinal()] = .6;
		  matrix[xsd_unsignedInt.ordinal()][xsd_unsignedShort.ordinal()] = .8;
		  matrix[xsd_unsignedInt.ordinal()][xsd_unsignedByte.ordinal()] = .7;
		  matrix[xsd_unsignedShort.ordinal()][xsd_unsignedByte.ordinal()] = .8;
		  matrix[xsd_hexBinary.ordinal()][xsd_base64Binary.ordinal()] = .9;
		  matrix[xsd_dateTime.ordinal()][xsd_time.ordinal()] = .5;
		  matrix[xsd_dateTime.ordinal()][xsd_date.ordinal()] = .5;
		  matrix[xsd_gYearMonth.ordinal()][xsd_gYear.ordinal()] = .5;
		  matrix[xsd_gYearMonth.ordinal()][xsd_gMonthDay.ordinal()] = .5;
		  matrix[xsd_gYearMonth.ordinal()][xsd_gMonth.ordinal()] = .5;
		  matrix[xsd_gMonthDay.ordinal()][xsd_gDay.ordinal()] = .5;
		  matrix[xsd_gMonthDay.ordinal()][xsd_gMonth.ordinal()] = .5;
		 
		  for (int i = 0; i < nTypes; i++){
			  matrix [i][i] = 1;
			  for (int j = 0; j < i; j++) matrix [i][j] = matrix[j][i];
		  } // for 
	  } // static

	  public static double compatibility(String type1, String type2){
		  return matrix[dataType.valueOf(type1).ordinal()][dataType.valueOf(type2).ordinal()];
	  }
	}
	
	public static double CompatibilityMatch(String type1, String type2){
		if (types.size() == 0) types = getEnums();
		if (!types.contains("xsd_" + type1) || !types.contains("xsd_" + type2)) return 0.0D;
		return TypeCompatibility.dataType.compatibility("xsd_" + type1, "xsd_" + type2);
	}

	public static HashSet<String> getEnums() {
		HashSet<String> values = new HashSet<String>();
		for (dataType str : dataType.values()) {
			values.add(str.toString());
		}
		return values;
	}

	
	public static void main(String[] args){
		int nTypes = dataType.values().length;
		/*
		for (int i = 0; i < nTypes; i++){
			for (int j = 0; j < nTypes; j++){
				out.println(dataType.values()[i] + "   " + dataType.values()[j] + " = " + dataType.matrix[i][j]);
			}
		}
		*/
		out.println(CompatibilityMatch("double", "float"));
	}
}
