package com.amazonaws.samples;



import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mediaconvert.*;
import com.amazonaws.services.mediaconvert.model.*;

public class MediaConvertTestJobCreate {

	private static final String mcRoleARN = "arn:aws:iam::111122223333:role/MediaConvert_Role";
	private static final String fileInput = "s3://input-bucket/test.mov";
	private static final String fileOutput = "s3://bucket-out/javasdk/out";
    
	
	public static void main(String[] args)  {
				
		try {
			

			//Get endpoint 
			//Warning DO NOT call this endpoint continuously, call it once when you initialize application and save endpoint off externally
			//and read endpoint from there, you may hit an API throttle  if you make consecutive calls to the describe endpoints API. 
		   String region = "us-west-2";
		   AWSMediaConvert emf = AWSMediaConvertClientBuilder.standard()
					.withRegion(region)
					.build();
					 
		   DescribeEndpointsRequest request = new DescribeEndpointsRequest();
			
		   String endpoint = emf.describeEndpoints(request)
				   					.getEndpoints()
				   					.get(0).getUrl();
			
			
			System.out.println(endpoint);
					
			EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpoint, region);
			emf  = AWSMediaConvertClientBuilder.standard()
			     .withEndpointConfiguration(endpointConfiguration)
			     .build();
					
			// Create job Request
			CreateJobRequest createJobRequest = new CreateJobRequest();
			
			//Assign role to use in the service
			createJobRequest.withRole(mcRoleARN);
			
			//Add user metadata to job request
			createJobRequest.addUserMetadataEntry("Customer", "Amazon");
			
			//Set Timecode config for Input
			TimecodeConfig jobSettingsTC = new TimecodeConfig()
					.withSource("EMBEDDED");
			
			//create job settings
			JobSettings jobSettings = new JobSettings()
					.withAdAvailOffset(0)
					.withTimecodeConfig(jobSettingsTC);
			
			createJobRequest.withSettings(jobSettings);
			
			
			//create file group settings
			FileGroupSettings jobFileGroupSettings = new FileGroupSettings()
					.withDestination(fileOutput);
			
			//create file output group 
			OutputGroup jobOutputGroup = new OutputGroup()
					.withName("File Group");
			
			//create output group settings
			OutputGroupSettings jobOutputSettings = new OutputGroupSettings()
					.withFileGroupSettings(jobFileGroupSettings)
					.withType("FILE_GROUP_SETTINGS");
			
			jobOutputGroup.withOutputGroupSettings(jobOutputSettings);
			
			
			
			//Set H264 Settings
			H264Settings h264CodecSettings = new H264Settings()
					.withBitrate(2000000)
					.withRateControlMode("CBR")
					.withCodecProfile("MAIN")
					.withCodecLevel("AUTO")
					.withFramerateControl("SPECIFIED")
					.withFramerateDenominator(1)
					.withFramerateNumerator(30)
					.withParControl("INITIALIZED_FROM_SOURCE")
					.withGopSizeUnits("FRAMES")
					.withGopClosedCadence(1)
					.withGopSize(90.0)
					.withInterlaceMode("PROGRESSIVE")
					.withQualityTuningLevel("SINGLE_PASS")
					.withAdaptiveQuantization("MEDIUM")
					.withFieldEncoding("DUPLICATE_DROP")
					.withSceneChangeDetect("TRUE")
					.withEntropyEncoding("CABAC")
					.withSlices(1)
					.withNumberBFramesBetweenReferenceFrames(1)
					.withNumberReferenceFrames(2);
					
			
			//Set Video Codec Settings
			VideoCodecSettings videoCodecSettings = new VideoCodecSettings()
					.withCodec("H_264")
					.withH264Settings(h264CodecSettings);
					
			//Create VideoDescription
			VideoDescription videoSettings = new VideoDescription()
					.withAntiAlias("ENABLED")
					.withWidth(1280)
					.withHeight(720)
					.withColorMetadata("ENABLED")
					.withCodecSettings(videoCodecSettings)
					.withScalingBehavior("DEFAULT")
					.withTimecodeInsertion("PIC_TIMING_SEI")
					.withColorMetadata("INSERT")
					.withRespondToAfd("NONE")
					.withAfdSignaling("NONE");
					
			
			
			//Set AAC Settings
			AacSettings aacAudioSettings = new AacSettings()
					.withCodecProfile("LC")
					.withBitrate(96000)
					.withSampleRate(48000)
					.withRateControlMode("CBR")
					.withCodingMode("CODING_MODE_2_0")
					.withRawFormat("NONE")
					.withSpecification("MPEG4")
					.withAudioDescriptionBroadcasterMix("NORMAL");

			
			//Set Audio Codec Settings
			AudioCodecSettings audioCodecSettings = new AudioCodecSettings()
					.withAacSettings(aacAudioSettings)
					.withCodec("AAC");
			
			//Create Audio Description
			AudioDescription audioSettings = new AudioDescription()
					.withAudioSourceName("Audio Selector 1")
					.withLanguageCode("eng")
					.withStreamName("English")
					.withCodecSettings(audioCodecSettings);

			
			
			//Create Output
			Output jobOutput = new Output()
					.withNameModifier("_Test")
					.withAudioDescriptions(audioSettings)
					.withVideoDescription(videoSettings);
			

			
			

	 
			 //create Mp4 Container
			 ContainerSettings jobContainerSettings = new ContainerSettings()
					.withContainer("MP4");
			
			 
			 Mp4Settings mp4 = new Mp4Settings()
					 .withFreeSpaceBox("EXCLUDE")
					 .withCslgAtom("INCLUDE")
					 .withMoovPlacement("PROGRESSIVE_DOWNLOAD");
			
			//set Container Settings
			jobContainerSettings.setMp4Settings(mp4);
			jobOutput.withContainerSettings(jobContainerSettings);
			
			//set Output to Output Groups
			jobOutputGroup.withOutputs(jobOutput);
			jobSettings.withOutputGroups(jobOutputGroup);
			
			
			
			//create Input
			Input input = new Input()
					.withFilterEnable("DISABLED")
					.withPsiControl("USE_PSI")
					.withFilterStrength(0)
					.withDeblockFilter("DISABLED")
					.withDenoiseFilter("DISABLED")
					.withTimecodeSource("EMBEDDED")
					.withFileInput(fileInput)
					.withFilterEnable("AUTO");
			
	
			//Create Audio Selector
			AudioSelector inputAudioSelector = new AudioSelector()
					.withTracks(1)
					.withOffset(0)
					.withDefaultSelection("NOT_DEFAULT")
					.withProgramSelection(1)
					.withSelectorType("TRACK");
			
			input.addAudioSelectorsEntry("Audio Selector 1", inputAudioSelector);
	
			//Create Video Selector
			VideoSelector inputVideoSelector = new VideoSelector()
					.withColorSpace("FOLLOW");
			input.withVideoSelector(inputVideoSelector);
			
			//Add all Input settings to Job Settings	
			jobSettings.withInputs(input);
			
			//Create Job request
			CreateJobResult createJobResult = emf.createJob(createJobRequest);
			System.out.println(createJobResult);
					
						
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with the services, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}


}
