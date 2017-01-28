################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/SimpleVideoPipilineRecorder.cpp 

OBJS += \
./src/SimpleVideoPipilineRecorder.o 

CPP_DEPS += \
./src/SimpleVideoPipilineRecorder.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/usr/include/gstreamer-1.0 -I/usr/include/glib-2.0 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<" `pkg-config --cflags --libs glib-2.0`
	@echo 'Finished building: $<'
	@echo ' '


