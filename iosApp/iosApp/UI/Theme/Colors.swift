import SwiftUI

// MARK: - ErgoGuard Theme Colors
// M3-inspired Cyan & Emerald color palette for iOS
// Based on Android theme colors for consistency

struct ErgoGuardColors {
    
    // MARK: - Primary Colors (Cyan-based)
    static let primary = Color(hex: "006874")
    static let onPrimary = Color.white
    static let primaryContainer = Color(hex: "97F0FF")
    static let onPrimaryContainer = Color(hex: "001F24")
    
    // MARK: - Secondary Colors (Slate/Neutral)
    static let secondary = Color(hex: "4A6267")
    static let onSecondary = Color.white
    static let secondaryContainer = Color(hex: "CDE7EC")
    static let onSecondaryContainer = Color(hex: "051F23")
    
    // MARK: - Tertiary Colors (Emerald CTA)
    static let tertiary = Color(hex: "006C4C")
    static let onTertiary = Color.white
    static let tertiaryContainer = Color(hex: "89F8C6")
    static let onTertiaryContainer = Color(hex: "002114")
    
    // MARK: - Error Colors
    static let error = Color(hex: "BA1A1A")
    static let onError = Color.white
    static let errorContainer = Color(hex: "FFDAD6")
    static let onErrorContainer = Color(hex: "410002")
    
    // MARK: - Warning Colors
    static let warning = Color(hex: "B98200")
    
    // MARK: - Background Colors
    static let background = Color(hex: "F8FDFF")
    static let onBackground = Color(hex: "001F25")
    
    // MARK: - Surface Colors
    static let surface = Color(hex: "F8FDFF")
    static let onSurface = Color(hex: "001F25")
    static let surfaceVariant = Color(hex: "DBE4E6")
    static let onSurfaceVariant = Color(hex: "3F484A")
    static let outline = Color(hex: "6F797A")
    
    // MARK: - Dark Theme Colors
    struct Dark {
        static let primary = Color(hex: "4FD8EB")
        static let onPrimary = Color(hex: "00363D")
        static let primaryContainer = Color(hex: "004F58")
        static let onPrimaryContainer = Color(hex: "97F0FF")
        
        static let secondary = Color(hex: "B1CBD0")
        static let secondaryContainer = Color(hex: "334B4F")
        
        static let tertiary = Color(hex: "6CDAB6")
        static let tertiaryContainer = Color(hex: "005138")
        
        static let background = Color(hex: "001F25")
        static let onBackground = Color(hex: "A6EEFF")
        static let surface = Color(hex: "001F25")
        static let onSurface = Color(hex: "A6EEFF")
        static let surfaceVariant = Color(hex: "3F484A")
        static let onSurfaceVariant = Color(hex: "BFC8CA")
    }
}

// MARK: - Severity Level
enum SeverityLevel: String, CaseIterable {
    case normal = "NORMAL"
    case warning = "WARNING"
    case danger = "DANGER"
    
    var color: Color {
        switch self {
        case .normal:
            return ErgoGuardColors.tertiary
        case .warning:
            return ErgoGuardColors.warning
        case .danger:
            return ErgoGuardColors.error
        }
    }
    
    var label: LocalizedStringKey {
        switch self {
        case .normal:
            return "result_safe"
        case .warning:
            return "result_warning"
        case .danger:
            return "result_danger"
        }
    }
}

// MARK: - View Mode
enum ViewMode: String, CaseIterable {
    case side = "SIDE"
    case front = "FRONT"
    
    var label: LocalizedStringKey {
        switch self {
        case .side:
            return "mode_side"
        case .front:
            return "mode_front"
        }
    }
}
