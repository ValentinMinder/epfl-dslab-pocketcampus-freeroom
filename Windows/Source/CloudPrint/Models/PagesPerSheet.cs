using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum PagesPerSheet
    {
        Two = 2,
        Four = 4,
        Six = 6,
        Nine = 9,
        Sixteen = 16
    }
}