//package slimeknights.mantle.lib.util;
//
//import com.google.common.collect.ImmutableList;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.mojang.math.Transformation;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
//import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
//import net.minecraft.client.renderer.block.model.BakedQuad;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.core.Direction;
//
//import java.util.Arrays;
//
//@Environment(EnvType.CLIENT)
//public class ItemLayerUtil {
//
//  private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
//  private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};
//
//  public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform, boolean fullbright)
//  {
//    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
//
//    int uMax = sprite.getWidth();
//    int vMax = sprite.getHeight();
//
//    FaceData faceData = new FaceData(uMax, vMax);
//    boolean translucent = false;
//
//    for(int f = 0; f < sprite.getFrameCount(); f++)
//    {
//      boolean ptu;
//      boolean[] ptv = new boolean[uMax];
//      Arrays.fill(ptv, true);
//      for(int v = 0; v < vMax; v++)
//      {
//        ptu = true;
//        for(int u = 0; u < uMax; u++)
//        {
//          int alpha = sprite.getPixelRGBA(f, u, vMax - v - 1) >> 24 & 0xFF;
//          boolean t = alpha / 255f <= 0.1f;
//
//          if (!t && alpha < 255)
//          {
//            translucent = true;
//          }
//
//          if(ptu && !t) // left - transparent, right - opaque
//          {
//            faceData.set(Direction.WEST, u, v);
//          }
//          if(!ptu && t) // left - opaque, right - transparent
//          {
//            faceData.set(Direction.EAST, u-1, v);
//          }
//          if(ptv[u] && !t) // up - transparent, down - opaque
//          {
//            faceData.set(Direction.UP, u, v);
//          }
//          if(!ptv[u] && t) // up - opaque, down - transparent
//          {
//            faceData.set(Direction.DOWN, u, v-1);
//          }
//
//          ptu = t;
//          ptv[u] = t;
//        }
//        if(!ptu) // last - opaque
//        {
//          faceData.set(Direction.EAST, uMax-1, v);
//        }
//      }
//      // last line
//      for(int u = 0; u < uMax; u++)
//      {
//        if(!ptv[u])
//        {
//          faceData.set(Direction.DOWN, u, vMax-1);
//        }
//      }
//    }
//
//    // horizontal quads
//    for (Direction facing : HORIZONTALS)
//    {
//      for (int v = 0; v < vMax; v++)
//      {
//        int uStart = 0, uEnd = uMax;
//        boolean building = false;
//        for (int u = 0; u < uMax; u++)
//        {
//          boolean face = faceData.get(facing, u, v);
//          if (!translucent)
//          {
//            if (face)
//            {
//              if (!building)
//              {
//                building = true;
//                uStart = u;
//              }
//              uEnd = u + 1;
//            }
//          }
//          else
//          {
//            if (building && !face) // finish current quad
//            {
//              // make quad [uStart, u]
//              int off = facing == Direction.DOWN ? 1 : 0;
//              builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v+off, u-uStart, fullbright));
//              building = false;
//            }
//            else if (!building && face) // start new quad
//            {
//              building = true;
//              uStart = u;
//            }
//          }
//        }
//        if (building) // build remaining quad
//        {
//          // make quad [uStart, uEnd]
//          int off = facing == Direction.DOWN ? 1 : 0;
//          builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v+off, uEnd-uStart, fullbright));
//        }
//      }
//    }
//
//    // vertical quads
//    for (Direction facing : VERTICALS)
//    {
//      for (int u = 0; u < uMax; u++)
//      {
//        int vStart = 0, vEnd = vMax;
//        boolean building = false;
//        for (int v = 0; v < vMax; v++)
//        {
//          boolean face = faceData.get(facing, u, v);
//          if (!translucent)
//          {
//            if (face)
//            {
//              if (!building)
//              {
//                building = true;
//                vStart = v;
//              }
//              vEnd = v + 1;
//            }
//          }
//          else
//          {
//            if (building && !face) // finish current quad
//            {
//              // make quad [vStart, v]
//              int off = facing == Direction.EAST ? 1 : 0;
//              builder.add(buildSideQuad(transform, facing, tint, sprite, u+off, vStart, v-vStart, fullbright));
//              building = false;
//            }
//            else if (!building && face) // start new quad
//            {
//              building = true;
//              vStart = v;
//            }
//          }
//        }
//        if (building) // build remaining quad
//        {
//          // make quad [vStart, vEnd]
//          int off = facing == Direction.EAST ? 1 : 0;
//          builder.add(buildSideQuad(transform, facing, tint, sprite, u+off, vStart, vEnd-vStart, fullbright));
//        }
//      }
//    }
//
//    // front
//    builder.add(buildQuad(transform, Direction.NORTH, sprite, tint, fullbright,
//      0, 0, 7.5f / 16f, sprite.getU0(), sprite.getV1(),
//      0, 1, 7.5f / 16f, sprite.getU0(), sprite.getV0(),
//      1, 1, 7.5f / 16f, sprite.getU1(), sprite.getV0(),
//      1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1()
//    ));
//    // back
//    builder.add(buildQuad(transform, Direction.SOUTH, sprite, tint, fullbright,
//      0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),
//      1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),
//      1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),
//      0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0()
//    ));
//
//    return builder.build();
//  }
//
//  private static BakedQuad buildSideQuad(Transformation transform, Direction side, int tint, TextureAtlasSprite sprite, int u, int v, int size, boolean fullbright)
//  {
//    final float eps = 1e-2f;
//
//    int width = sprite.getWidth();
//    int height = sprite.getHeight();
//
//    float x0 = (float) u / width;
//    float y0 = (float) v / height;
//    float x1 = x0, y1 = y0;
//    float z0 = 7.5f / 16f, z1 = 8.5f / 16f;
//
//    switch(side)
//    {
//      case WEST:
//        z0 = 8.5f / 16f;
//        z1 = 7.5f / 16f;
//      case EAST:
//        y1 = (float) (v + size) / height;
//        break;
//      case DOWN:
//        z0 = 8.5f / 16f;
//        z1 = 7.5f / 16f;
//      case UP:
//        x1 = (float) (u + size) / width;
//        break;
//      default:
//        throw new IllegalArgumentException("can't handle z-oriented side");
//    }
//
//    float dx = side.getNormal().getX() * eps / width;
//    float dy = side.getNormal().getY() * eps / height;
//
//    float u0 = 16f * (x0 - dx);
//    float u1 = 16f * (x1 - dx);
//    float v0 = 16f * (1f - y0 - dy);
//    float v1 = 16f * (1f - y1 - dy);
//
//    return buildQuad(
//      transform, remap(side), sprite, tint, fullbright,
//      x0, y0, z0, sprite.getU(u0), sprite.getV(v0),
//      x1, y1, z0, sprite.getU(u1), sprite.getV(v1),
//      x1, y1, z1, sprite.getU(u1), sprite.getV(v1),
//      x0, y0, z1, sprite.getU(u0), sprite.getV(v0)
//    );
//  }
//
//  private static BakedQuad buildQuad(Transformation transform, Direction side, TextureAtlasSprite sprite, int tint, boolean fullbright,
//                                     float x0, float y0, float z0, float u0, float v0,
//                                     float x1, float y1, float z1, float u1, float v1,
//                                     float x2, float y2, float z2, float u2, float v2,
//                                     float x3, float y3, float z3, float u3, float v3)
//  {
////    BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
////
////    builder.setQuadTint(tint);
////    builder.setQuadOrientation(side);
////    builder.setApplyDiffuseLighting(false);
////
////    boolean hasTransform = !transform.isIdentity();
////    IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;
////
////    int uLight, vLight;
////    uLight = vLight = fullbright ? 15 : 0;
////
////    putVertex(consumer, side, x0, y0, z0, u0, v0, uLight, vLight);
////    putVertex(consumer, side, x1, y1, z1, u1, v1, uLight, vLight);
////    putVertex(consumer, side, x2, y2, z2, u2, v2, uLight, vLight);
////    putVertex(consumer, side, x3, y3, z3, u3, v3, uLight, vLight);
//
//    QuadEmitter emitter = RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();
//    emitter.nominalFace(side);
//    int uLight, vLight;
//    uLight = vLight = fullbright ? 15 : 0;
//    emitter.pos(0, x0, y0, z0);
//    emitter.spriteU(0, uLight);
//    emitter.spriteV(0, vLight);
//    emitter.nominalFace(side);
//    emitter.emit();
//    emitter.pos(1, x1, y1, z1);
//    emitter.spriteU(1, uLight);
//    emitter.spriteV(1, vLight);
//    emitter.nominalFace(side);
//    emitter.emit();
//    emitter.pos(2, x2, y2, z2);
//    emitter.spriteU(2, uLight);
//    emitter.spriteV(2, vLight);
//    emitter.nominalFace(side);
//    emitter.emit();
//    emitter.pos(3, x3, y3, z3);
//    emitter.spriteU(3, uLight);
//    emitter.spriteV(3, vLight);
//    emitter.nominalFace(side);
//    emitter.emit();
//    return emitter.toBakedQuad(0, sprite, true);
//  }
//}
