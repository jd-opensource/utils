package utils.serialize.json;

public interface DynamicTypeConverter {

	/**
	 * 返回指定类型的序列化类型；
	 * 
	 * @param clazz
	 * @return
	 */
	Class<?> getSerializeType(Class<?> clazz);

}
