using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum MultiPageLayout
    {
        LeftToRightTopToBottom = 0,
        TopToBottomLeftToRight = 1,
        BottomToTopLeftToRight = 2,
        BottomToTopRightToLeft = 3,
        LeftToRightBottomToTop = 4,
        RightToLeftBottomToTop = 5,
        RightToLeftTopToBottom = 6,
        TopToBottomRightToLeft = 7
    }
}