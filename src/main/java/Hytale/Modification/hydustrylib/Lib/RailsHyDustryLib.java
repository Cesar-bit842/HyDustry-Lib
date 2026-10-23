package Hytale.Modification.hydustrylib.Lib;

import java.util.Map; 
import org.joml.Vector3d; 

public class RailsHyDustryLib { 
    @Deprecated
    public static Vector3d[] OrientRailsObsolete(Vector3d[] points, int direction) {
        Vector3d[] result = new Vector3d[points.length];
        final double CX = 0.5;
        final double CZ = 0.5;

        for (int i = 0; i < points.length; i++) {
            Vector3d p = points[i];

            
            double tx = p.x - CX;
            double tz = p.z - CZ;

            double rx, rz;

            
            switch (direction) {
                case 0: 
                    rx = tx;
                    rz = tz;
                    break;

                case 1: 
                    rx = tz;
                    rz = -tx;
                    break;

                case 2: 
                    rx = -tx;
                    rz = -tz;
                    break;

                case 3: 
                    rx = -tz;
                    rz = tx;
                    break;

                default:
                    throw new IllegalArgumentException("Dirección inválida (0=N, 1=W, 2=S, 3=E)");
            }

            
            double nx = CX + rx;
            double nz = CZ + rz;
            double ny = p.y; 

            result[i] = new Vector3d(nx, ny, nz);
        }

        return result;
    }
    public static Vector3d RailsCalculateNextPosition(Vector3d[] rotatedPoints, double progressPercent, int smoothing) {
        if (rotatedPoints == null || rotatedPoints.length < 3) {
            return new Vector3d(0.5, 0.5, 0.5);
        }

        
        double t = progressPercent / 100.0;
        if (t < 0.0) t = 0.0;
        if (t > 1.0) t = 1.0;

        Vector3d p0 = rotatedPoints[0]; 
        Vector3d p1 = rotatedPoints[1]; 
        Vector3d p2 = rotatedPoints[2]; 

        
        double rectX, rectY, rectZ;
        if (t <= 0.5) {
            double localT = t / 0.5; 
            rectX = p0.x + (p1.x - p0.x) * localT;
            rectY = p0.y + (p1.y - p0.y) * localT; 
            rectZ = p0.z + (p1.z - p0.z) * localT;
        } else {
            double localT = (t - 0.5) / 0.5;
            rectX = p1.x + (p2.x - p1.x) * localT;
            rectY = p1.y + (p2.y - p1.y) * localT; 
            rectZ = p1.z + (p2.z - p1.z) * localT;
        }

        
        
        boolean isStraightX = Math.abs((p0.x + p2.x) / 2.0 - p1.x) < 0.001;
        boolean isStraightY = Math.abs((p0.y + p2.y) / 2.0 - p1.y) < 0.001;
        boolean isStraightZ = Math.abs((p0.z + p2.z) / 2.0 - p1.z) < 0.001;

        
        
        if (smoothing <= 0 || (isStraightX && isStraightY && isStraightZ)) {
            return new Vector3d(rectX, rectY, rectZ);
        }

        
        double u = 1.0 - t;
        double tt = t * t;
        double uu = u * u;
        double curveX = (uu * p0.x) + (2.0 * u * t * p1.x) + (tt * p2.x);
        double curveY = (uu * p0.y) + (2.0 * u * t * p1.y) + (tt * p2.y); 
        double curveZ = (uu * p0.z) + (2.0 * u * t * p1.z) + (tt * p2.z);

        
        int clampedSmooth = Math.max(0, Math.min(10, smoothing));
        double blendFactor = clampedSmooth / 10.0;

        double finalX = rectX + (curveX - rectX) * blendFactor;
        double finalY = rectY + (curveY - rectY) * blendFactor; 
        double finalZ = rectZ + (curveZ - rectZ) * blendFactor;

        return new Vector3d(finalX, finalY, finalZ);
    }
    public static Vector3d[] OrientRails(Vector3d[] points, int direction) {
        if (points == null) return new Vector3d[0];
        
        Vector3d[] result = new Vector3d[points.length];
        final double CX = 0.5;
        final double CY = 0.5; 
        final double CZ = 0.5;

        
        
        boolean invertY = (direction >= 8 && direction <= 63);

        
        
        int horizontalDir = Math.abs(direction) % 4;

        for (int i = 0; i < points.length; i++) {
            Vector3d p = points[i];

            
            double tx = p.x - CX;
            double tz = p.z - CZ;

            double rx, rz;

            
            switch (horizontalDir) {
                case 0: 
                    rx = tx;
                    rz = tz;
                    break;

                case 1: 
                    rx = tz;
                    rz = -tx;
                    break;

                case 2: 
                    rx = -tx;
                    rz = -tz;
                    break;

                case 3: 
                    rx = -tz;
                    rz = tx;
                    break;

                default:
                    
                    rx = tx;
                    rz = tz;
                    break;
            }

            
            double nx = CX + rx;
            double nz = CZ + rz;
            
            
            double ny;
            if (invertY) {
                
                
                ny = 1.0 - p.y; 
            } else {
                ny = p.y; 
            }

            result[i] = new Vector3d(nx, ny, nz);
        }

        return result;
    }
    public static Vector3d[] OrientGlobalPositions(Vector3d[] points, int direction) {
        if (points == null) return new Vector3d[0];
        
        Vector3d[] result = new Vector3d[points.length]; 
        boolean invertY = (direction >= 8 && direction <= 63); 
        int horizontalDir = Math.abs(direction) % 4;

        for (int i = 0; i < points.length; i++) {
            Vector3d p = points[i];
 
            double tx = p.x;
            double tz = p.z;

            double rx, rz;
 
            switch (horizontalDir) {
                case 0: 
                    rx = -tx;
                    rz = -tz;
                    break;

                case 1:  
                    rx = -tz;
                    rz = tx;
                    break;

                case 2:  
                    rx = tx;
                    rz = tz;
                    break;
                   
                case 3:   
                    rx = tz;
                    rz = -tx;
                    break;

                default:
                    rx = tx;
                    rz = tz;
                    break;
            }
 
            double ny = invertY ? -p.y : p.y; 
            result[i] = new Vector3d(rx, ny, rz);
        }

        return result;
    }
    
    public static Vector3d[] TagsPathsConversion(Map<String, String[]> Tags) {  
        if(!Tags.containsKey("HyDustryUtility.Paths")){
            return new Vector3d[]{
                new Vector3d(0.5, 0.5, 0.5)
            };
        }  

        String[] Paths = Tags.get("HyDustryUtility.Paths"); 
        if(Paths == null || Paths.length <= 0){ 
            return new Vector3d[]{
                new Vector3d(0.5, 0.5, 0.5)
            }; 
        }

        Vector3d[] Conversion = new Vector3d[Paths.length];  
        for (int i = 0; i < Paths.length; i++) {
            String[] PathCords = Paths[i].replaceAll("\\s+", "").split(",");
            Conversion[i] = new Vector3d(
                Double.parseDouble(PathCords[0]), 
                    Double.parseDouble(PathCords[1]), 
                        Double.parseDouble(PathCords[2]));
        } 
        return Conversion;
    }    
    public static Vector3d[] ConvertingTagstoArrays(Map<String, String[]> Tags, String Tag) {  
        if(!Tags.containsKey("HyDustryUtility." + Tag)){
            return null;
        }  

        String[] Paths = Tags.get("HyDustryUtility." + Tag); 
        if(Paths == null || Paths.length <= 0){ 
            return null;
        }

        Vector3d[] Conversion = new Vector3d[Paths.length];  
        for (int i = 0; i < Paths.length; i++) {
            String[] PathCords = Paths[i].replaceAll("\\s+", "").split(",");
            Conversion[i] = new Vector3d(
                Double.parseDouble(PathCords[0]), 
                    Double.parseDouble(PathCords[1]), 
                        Double.parseDouble(PathCords[2]));
        } 
        return Conversion;
    }    
    public static Vector3d[] ConvertingTagsPositionstoArrays(Map<String, String[]> Tags, String Tag) {  
        if(!Tags.containsKey("HyDustryUtility." + Tag)){
            return null;
        }  

        String[] Paths = Tags.get("HyDustryUtility." + Tag); 
        if(Paths == null || Paths.length <= 0){ 
            return null;
        }

        Vector3d[] Conversion = new Vector3d[Paths.length];  
        for (int i = 0; i < Paths.length; i++) {
            String[] PathCords = Paths[i].replaceAll("[()\\s+]", "").split(",");
            Conversion[i] = new Vector3d(
                Double.parseDouble(PathCords[0]), 
                    Double.parseDouble(PathCords[1]), 
                        Double.parseDouble(PathCords[2]));
        } 
        return Conversion;
    }    
    public static String vectorArrayToString(Vector3d[] vectors) {
        if (vectors == null || vectors.length == 0) {
            return "[]";
        } 
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vectors.length; i++) {
            Vector3d v = vectors[i];
            sb.append("(")
            .append(v.x).append(", ")
            .append(v.y).append(", ")
            .append(v.z).append(")");
            if (i < vectors.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
