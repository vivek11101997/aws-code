package com.amazonaws.samples;



import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mediaconvert.*;
import com.amazonaws.services.mediaconvert.model.*;

public class MediaConvertTestPresets {

	private static final String mcRoleARN = "arn:aws:iam::111122223333:role/EMFRoleSPNames";
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
			
			//Create Output
			
			Output jobOutput = new Output()
					.withNameModifier("_SD")
					.withPreset("System-Generic_Sd_Mp4_Avc_Aac_4x3_640x480p_24Hz_1.5Mbps");
			
			Output jobOutput1 = new Output()
					.withNameModifier("_HD")
					.withPreset("System-Generic_Hd_Mp4_Avc_Aac_16x9_1280x720p_24Hz_4.5Mbps");
	 
			 //create Mp4 Container
			 ContainerSettings jobContainerSettings = new ContainerSettings()
					.withContainer("MP4");
			
			 
			 Mp4Settings mp4 = new Mp4Settings()
					 .withFreeSpaceBox("EXCLUDE")
					 .withCslgAtom("INCLUDE")
					 .withMoovPlacement("PROGRESSIVE_DOWNLOAD");
			 
			jobContainerSettings.setMp4Settings(mp4);
			jobOutput.withContainerSettings(jobContainerSettings);
	
			jobOutputGroup.withOutputs(jobOutput, jobOutput1);
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
