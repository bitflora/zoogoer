package net.bitflora.zoogoer.recipe;

// GemPolishingRecipe is stubbed out for the 1.21.1 port.
// The Recipe interface was substantially refactored in 1.21.1:
//   - getId() was removed from the interface
//   - fromJson/fromNetwork/toNetwork replaced by MapCodec + StreamCodec
//   - canCraftInDimensions() removed
//   - FriendlyByteBuf replaced by RegistryFriendlyByteBuf
//   - assemble/getResultItem take HolderLookup.Provider instead of RegistryAccess
//   - ShapedRecipe.itemStackFromJson() removed
// A full rewrite to MapCodec/StreamCodec is needed before re-enabling this class.
public class GemPolishingRecipe {
    private GemPolishingRecipe() {}
}
