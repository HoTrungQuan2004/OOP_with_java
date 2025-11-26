package com.parkingapp.enums;

/**
 * Trạng thái chỗ đỗ
 */
public enum SpotStatus {
    FREE,          // rảnh
    OCCUPIED,      // đang có xe đậu
    ASSIGNED,      // được gán cho cư dân (chưa chắc đang đậu)
    OUT_OF_SERVICE // hỏng / bảo trì
}
