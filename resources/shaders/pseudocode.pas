procedure TraceRay(AxisAlignedBoundingBox) begin
	// Отцентрированые и нормализированые координаты луча
	tx = X / screenWidth * 2.0 - 1.0;
	ty = Y / screenHeight * 2.0 - 1.0;
	vec3 rayOrigin = ToObjectSpace(vec3(tx, ty, 0.0));
	vec3 rayDirection = normalize(ToObjectSpace(vec3(tx, ty, -1.0)) - rayOrigin);
	
	// Вычисление прирощения для луча
	vec3 modulation = rayDirection / sampleResolution;
	
	vec3 Color = vec3(1, 1, 1);
	Alpha = 0;
	
	
	vec3 x1 = First(rayOrigin, rayDirection, AxisAlignedBoundingBox);
	vec3 x2 = Last(rayOrigin, rayDirection, AxisAlignedBoundingBox);
	
	// Расчет всех проб попадающих в область модели
	while (ToImageSpace(x1).z > ToImageSpace(x2).z) do
		begin
		// Вычисление пробы и добавление значения к показателю прозрачности
		Alpha += Sample(x1);
		// Если прозрачность равна нулю, то прервать выполнение алгоритма
		if(Alpha >= 1) break;
		// Увеличение координаты пробы
		x1 += modulation;
	end
end