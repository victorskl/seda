using Microsoft.Azure.Mobile.Server;
/**
 * SEDA sampling data
 * Each sample has startTime and endTime to denote the particular type of
 * sensor data sample recording. 
 * 
 * The sample type define, the type of the trigger event using one or 
 * more sensors at a particular event occur at driving condition.
 * Currently map to:
 * 1 = HEAD_CHECK_NEG_CNT
 * 2 = HEAD_CHECK_POS_CNT
 * 3 = CAR_DISTANCE_NEG_CNT
 * 
 * This the counterpart of the Android Java classes: Sample.java and SampleType.java.
 **/
namespace SedaBackendService.DataObjects
{
    public class Sample : EntityData
    {
        public string text { get; set; }

        public bool complete { get; set; }

        public long startTime { get; set; }

        public long endTime { get; set; }

        public int count { get; set; }

        public int sampleType { get; set; }
    }
}