cmake_minimum_required(VERSION 3.10.2)

project(airallan_native)

add_library(
    airallan_native
    SHARED
    native_receiver_stub.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    airallan_native
    ${log-lib}
)