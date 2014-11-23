using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "CloudPrintMultiPageConfig" )]
    public sealed class MultiPageConfig
    {
        [ThriftField( 1, true, "nbPagesPerSheet" )]
        public PagesPerSheet PagesPerSheet { get; set; }

        [ThriftField( 2, true, "layout" )]
        public MultiPageLayout Layout { get; set; }
    }
}