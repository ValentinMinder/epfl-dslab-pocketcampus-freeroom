using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum DoubleSidedConfig
    {
        SingleSide = 0, // not supported by the server; for UI purposes only
        LongEdge = 1,
        ShortEdge = 2
    }
}